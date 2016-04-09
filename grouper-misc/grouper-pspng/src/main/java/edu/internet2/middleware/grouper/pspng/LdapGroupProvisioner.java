package edu.internet2.middleware.grouper.pspng;

/*******************************************************************************
 * Copyright 2015 Internet2
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModificationType;
import org.ldaptive.Connection;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.ModifyRequest;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResult;
import org.ldaptive.io.LdifReader;

import edu.internet2.middleware.grouper.Group;
import edu.internet2.middleware.grouper.util.GrouperUtil;
import edu.internet2.middleware.subject.Subject;



/**
 * This class is the workhorse for provisioning LDAP groups from
 * grouper.
 *  
 * @author bert
 *
 */
public class LdapGroupProvisioner extends LdapProvisioner {
  LdapGroupProvisionerProperties config;
  
  /**
   * What class holds what is necessary for our configuration
   * @return
   */
  public static Class<? extends ProvisionerProperties> getPropertyClass() {
    return LdapGroupProvisionerProperties.class;
  }
  
  
  public LdapGroupProvisioner(String provisionerName, LdapGroupProvisionerProperties config) {
    super(provisionerName, config);

    this.config = config;

    System.err.println(String.format("Constructing LdapGroupProvisioner:%s", provisionerName));
  }


  @Override
  protected void addMembership(Group group, TargetSystemGroup tsGroup,
      Subject subject, TargetSystemUser tsUser) throws PspException {
    if ( tsGroup == null )
      tsGroup = createGroup(group);

    LdapGroup ldapGroup = (LdapGroup) tsGroup;
    
    // TODO: Look in memory cache to see if change is necessary: 
    // a) User object's group-listing attribute
    // or b) if the group-membership attribute is being fetched
    
    String membershipAttributeValue = evaluateJexlExpression(config.getMemberAttributeValueFormat(), subject, group);
    scheduleGroupModification(group, ldapGroup, AttributeModificationType.ADD, Arrays.asList(membershipAttributeValue));
  }

  
  protected void scheduleGroupModification(Group group, LdapGroup ldapGroup, AttributeModificationType modType, Collection<String> membershipValuesToChange) {
    uncacheGroup(group, ldapGroup);
    
    String attributeName = config.getMemberAttributeName();
    
    for ( String value : membershipValuesToChange )
      // ADD/REMOVE <value> to/from <attribute> of <group>
      LOG.info("Will change LDAP: {} {} {} {} of {}", 
          modType, value,
          modType == AttributeModificationType.ADD ? "to" : "from",
          attributeName, ldapGroup);
    
    scheduleLdapModification(
        new ModifyRequest(
            ldapGroup.getLdapObject().getDn(),
            new AttributeModification(
                modType, 
                new LdapAttribute(attributeName, membershipValuesToChange.toArray(new String[0])))));
  }

  @Override
  protected void deleteMembership(Group group, TargetSystemGroup tsGroup,
      Subject subject, TargetSystemUser tsUser) throws PspException {
    if ( tsGroup == null ) {
      LOG.warn("{}: Ignoring request to remove {} from a group that doesn't exist: {}", getName(), subject.getId(), group.getName());
      return;
    }
    
    LdapGroup ldapGroup = (LdapGroup) tsGroup;
    

    // TODO: Look in memory cache to see if change is necessary: 
    // a) User object's group-listing attribute
    // or b) if the group-membership attribute is being fetched
    
    String membershipAttributeValue = evaluateJexlExpression(config.getMemberAttributeValueFormat(), subject, group);
    
    scheduleGroupModification(group, ldapGroup, AttributeModificationType.REMOVE, Arrays.asList(membershipAttributeValue));
  }
  
  
  @Override
  protected void doFullSync(
      Group group, TargetSystemGroup tsGroup, 
      Set<Subject> correctSubjects, Set<? extends TargetSystemUser> correctTSUsers) throws PspException {
    if ( tsGroup == null )
      tsGroup = createGroup(group);
    
    LdapGroup ldapGroup = (LdapGroup) tsGroup;
    
    Set<String> correctMembershipValues = new HashSet<String>();
    
    for ( Subject correctSubject: correctSubjects ) {
      String membershipAttributeValue = evaluateJexlExpression(config.getMemberAttributeValueFormat(), correctSubject, group);
      correctMembershipValues.add(membershipAttributeValue);
    }
    
    
    Collection<String> currentMembershipValues
      = ldapGroup.getLdapObject().getStringValues(config.getMemberAttributeName());
    
    // EXTRA = CURRENT - CORRECT
    {
      Collection<String> extraValues = new HashSet<String>(currentMembershipValues);
      extraValues.removeAll(correctMembershipValues);
      
      LOG.info("{}: Group {} has {} extra values", getName(), group.getName(), extraValues.size());
      if ( extraValues.size() > 0 )
        scheduleGroupModification(group, ldapGroup, AttributeModificationType.REMOVE, extraValues);
    }
    
    // MISSING = CORRECT - CURRENT
    {
      Collection<String> missingValues = new HashSet<String>(correctMembershipValues);
      missingValues.removeAll(currentMembershipValues);
      
      LOG.info("{}: Group {} has {} missing values", getName(), group.getName(), missingValues.size());
      if ( missingValues.size() > 0 )
        scheduleGroupModification(group, ldapGroup, AttributeModificationType.ADD, missingValues);
    }
  }
  
  
  @Override
  protected TargetSystemGroup createGroup(Group grouperGroup) throws PspException {
    LOG.info("Creating LDAP group for GrouperGroup: {} ", grouperGroup);
    String ldif = config.getGroupCreationLdifTemplate();
    ldif = ldif.replaceAll("\\|\\|", "\n");
    ldif = evaluateJexlExpression(ldif, null, grouperGroup);
    
    Connection conn = getLdapConnection();
    try {
      Reader reader = new StringReader(ldif);
      LdifReader ldifReader = new LdifReader(reader);
      SearchResult ldifResult = ldifReader.read();
      LdapEntry ldifEntry = ldifResult.getEntry();
      
      // Update DN to be relative to groupCreationBaseDn
      String actualDn = String.format("%s,%s", ldifEntry.getDn(),config.getGroupCreationBaseDn());
      ldifEntry.setDn(actualDn);
      
      LOG.info("Adding group: {}", ldifEntry);
      
      performLdapAdd(ldifEntry);
      
      // Read the group that was just created
      LOG.debug("Reading group that was just added to ldap server: {}", grouperGroup);
      return fetchTargetSystemGroup(grouperGroup);
    } catch (PspException e) {
      LOG.error("Problem while creating new group: {}: {}", ldif, e.getMessage());
      throw e;
    } catch ( IOException e ) {
      LOG.error("Problem while processing ldif to create new group: {}", ldif, e);
      throw new PspException("LDIF problem creating group: %s", e.getMessage());
    }
    finally {
      conn.close();
    }
  }

  @Override
  protected Map<Group, TargetSystemGroup> fetchTargetSystemGroups(
      Collection<Group> grouperGroupsToFetch) throws PspException {
    if ( grouperGroupsToFetch.size() > config.getGroupSearch_batchSize() )
      throw new IllegalArgumentException("LdapGroupProvisioner.fetchTargetSystemGroups: invoked with too many groups to fetch");
    
    // If this is a full-sync provisioner, then we want to make sure we get the member attribute of the
    // group so we see all members.
    String returnAttributes[] = config.getGroupSearchAttributes();
    if ( fullSyncMode ) {
      returnAttributes = Arrays.copyOf(returnAttributes, returnAttributes.length + 1);
      returnAttributes[returnAttributes.length-1] = config.getMemberAttributeName();
    }
    
    StringBuilder combinedLdapFilter = new StringBuilder();
    
    // Start the combined ldap filter as an OR-query
    combinedLdapFilter.append("(|");
    
    for ( Group grouperGroup : grouperGroupsToFetch ) {
      String f = getGroupLdapFilter(grouperGroup);
      
      if ( f == null || f.length() == 0 )
        continue;
      
      // Wrap the subject's filter in (...) if it doesn't start with (
      if ( f.startsWith("(") )
        combinedLdapFilter.append(f);
      else
        combinedLdapFilter.append('(').append(f).append(')');
    }
    combinedLdapFilter.append(')');

    // Actually do the search
    List<LdapObject> searchResult;
    
    LOG.info("{}: Searching for {} groups with:: {}", getName(), grouperGroupsToFetch.size(), combinedLdapFilter);
    
    try {
      searchResult = performLdapSearchRequest(
        new SearchRequest(config.getGroupSearchBaseDn(), 
              combinedLdapFilter.toString(), 
              returnAttributes));
    }
    catch (PspException e) {
      LOG.error("Problem fetching groups with filter {}", combinedLdapFilter);
      throw e;
    }

    LOG.debug("{}: Group search returned {} groups", getName(), searchResult.size());
    
    // Now we have a bag of LdapObjects, but we don't know which goes with which grouperGroup.
    // We're going to go through the Grouper Groups and their filters and compare
    // them to the Ldap data we've fetched into memory.
    Map<Group, TargetSystemGroup> result = new HashMap<Group, TargetSystemGroup>();
    
    Set<LdapObject> matchedFetchResults = new HashSet<LdapObject>();
    
    // For every group we tried to bulk fetch, find the matching LdapObject that came back
    for ( Group groupToFetch : grouperGroupsToFetch ) {
      String f = getGroupLdapFilter(groupToFetch);
      
      for ( LdapObject aFetchedLdapObject : searchResult ) {
        if ( aFetchedLdapObject.matchesLdapFilter(f) ) {
          result.put(groupToFetch, new LdapGroup(aFetchedLdapObject));
          matchedFetchResults.add(aFetchedLdapObject);
          break;
        }
      }
    }

    Set<LdapObject> unmatchedFetchResults = new HashSet<LdapObject>(searchResult);
    unmatchedFetchResults.removeAll(matchedFetchResults);
    
    for ( LdapObject unmatchedFetchResult : unmatchedFetchResults )
      LOG.error("{}: Group data from ldap server was not matched with a grouper group "
          + "(perhaps attributes are used in singleGroupSearchFilter ({}) that are not included "
          + "in groupSearchAttributes ({})?): {}",
          getName(), config.getSingleGroupSearchFilter(), config.getGroupSearchAttributes(), unmatchedFetchResult.getDn());
    
    return result;
}


  private String getGroupLdapFilter(Group grouperGroup) {
    String result = evaluateJexlExpression(config.getSingleGroupSearchFilter(), null, grouperGroup);
    if ( StringUtils.isEmpty(result) )
      throw new RuntimeException("Group searching requires singleGroupSearchFilter to be configured correctly");
    LOG.debug("{}: Filter for group {}: {}", getName(), grouperGroup.getName(), result);

    return result;
  }
}
