package edu.internet2.middleware.tierApiAuthzClient.testSuite;

import edu.internet2.middleware.tierApiAuthzClient.api.AsacApiVersionResource;
import edu.internet2.middleware.tierApiAuthzClient.contentType.AsacRestContentType;
import edu.internet2.middleware.tierApiAuthzClient.corebeans.AsacVersionResourceContainer;
import edu.internet2.middleware.tierApiAuthzClient.util.StandardApiClientUtils;

/**
 * test the version resource
 * @author mchyzer
 */
public class AsacTestSuiteVersionResource extends AsacTestSuiteResult {

  /**
   * 
   * @param results
   */
  public AsacTestSuiteVersionResource(AsacTestSuiteResults results) {
    super(results);
  }

  /**
   * test default resource
   */
  public void testVersionResourceJson() {
    
    helperTestVersionResource(AsacRestContentType.json);

  }

  /**
   * 
   * @param asacRestContentType
   */
  private void helperTestVersionResource(AsacRestContentType asacRestContentType) {

    AsacApiVersionResource asacApiVersionResource = new AsacApiVersionResource();
    asacApiVersionResource.setContentType(asacRestContentType);
    AsacVersionResourceContainer asacVersionResourceContainer = asacApiVersionResource
      .assignIndent(this.getResults().isIndent()).execute();
    
    executeTestsForHttp(200, asacRestContentType, "GET");
    
    executeTestsForServiceMeta(asacVersionResourceContainer);
    
    executeTestsForResponseMeta(asacVersionResourceContainer, 200);
    
    executeTestsForMeta(asacVersionResourceContainer, "SUCCESS", "versionResourceContainer", 
        "/" + StandardApiClientUtils.version() + "." + asacRestContentType.name(), true);

    assertNotNull("asasVersionResource", asacVersionResourceContainer.getVersionResource());
    assertEquals("asasVersionResource.entitiesUri", "/" + StandardApiClientUtils.version() + "/entities." + asacRestContentType.name(), 
        asacVersionResourceContainer.getVersionResource().getEntitiesUri());
    assertEquals("asasVersionResource.foldersUri", "/" + StandardApiClientUtils.version() + "/folders." + asacRestContentType.name(), 
        asacVersionResourceContainer.getVersionResource().getFoldersUri());
    assertEquals("asasVersionResource.groupsUri", "/" + StandardApiClientUtils.version() + "/groups." + asacRestContentType.name(), 
        asacVersionResourceContainer.getVersionResource().getGroupsUri());
    assertEquals("asasVersionResource.permissionsUri", "/" + StandardApiClientUtils.version() + "/permissions." + asacRestContentType.name(), 
        asacVersionResourceContainer.getVersionResource().getPermissionsUri());

  }
  
  /**
   * 
   */
  @Override
  public String getName() {
    return "versionResource";
  }
    
  
}
