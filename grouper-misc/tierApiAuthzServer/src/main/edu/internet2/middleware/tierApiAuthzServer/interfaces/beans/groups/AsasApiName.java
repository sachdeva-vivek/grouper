/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.tierApiAuthzServer.interfaces.beans.groups;

import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasName;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasUserContainer;


/**
 *
 */
public class AsasApiName {

  /**
   * 
   */
  public AsasApiName() {
  }

  private String formatted;

  private String familyName;

  private String givenName;
  
  private String middleName;
  
  private String honorificPrefix;
  
  private String honorificSuffix;

  
  /**
   * @return the formatted
   */
  public String getFormatted() {
    return this.formatted;
  }

  
  /**
   * @param formatted the formatted to set
   */
  public void setFormatted(String formatted) {
    this.formatted = formatted;
  }

  
  /**
   * @return the familyName
   */
  public String getFamilyName() {
    return this.familyName;
  }

  
  /**
   * @param familyName the familyName to set
   */
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  
  /**
   * @return the givenName
   */
  public String getGivenName() {
    return this.givenName;
  }

  
  /**
   * @param givenName the givenName to set
   */
  public void setGivenName(String givenName) {
    this.givenName = givenName;
  }

  
  /**
   * @return the middleName
   */
  public String getMiddleName() {
    return this.middleName;
  }

  
  /**
   * @param middleName the middleName to set
   */
  public void setMiddleName(String middleName) {
    this.middleName = middleName;
  }

  
  /**
   * @return the honorificPrefix
   */
  public String getHonorificPrefix() {
    return this.honorificPrefix;
  }

  
  /**
   * @param honorificPrefix the honorificPrefix to set
   */
  public void setHonorificPrefix(String honorificPrefix) {
    this.honorificPrefix = honorificPrefix;
  }

  
  /**
   * @return the honorificSuffix
   */
  public String getHonorificSuffix() {
    return this.honorificSuffix;
  }

  
  /**
   * @param honorificSuffix the honorificSuffix to set
   */
  public void setHonorificSuffix(String honorificSuffix) {
    this.honorificSuffix = honorificSuffix;
  }

  /**
   * convert the api beans to the transport beans
   * @param asasApiUser
   * @return the api bean
   */
  public static AsasName convertToAsasName(AsasApiName asasApiName) {
    if (asasApiName == null) {
      return null;
    }
    AsasName asasName = new AsasName();
    asasName.setFamilyName(asasApiName.getFamilyName());
    asasName.setFormatted(asasApiName.getFormatted());
    asasName.setGivenName(asasApiName.getGivenName());
    asasName.setHonorificPrefix(asasApiName.getHonorificPrefix());
    asasName.setHonorificSuffix(asasApiName.getHonorificSuffix());
    asasName.setMiddleName(asasApiName.getMiddleName());
    return asasName;
  }
}
