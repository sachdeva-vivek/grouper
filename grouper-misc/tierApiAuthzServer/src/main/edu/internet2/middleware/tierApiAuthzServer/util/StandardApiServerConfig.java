package edu.internet2.middleware.tierApiAuthzServer.util;

import edu.internet2.middleware.tierApiAuthzServer.config.AsasConfigPropertiesCascadeBase;


/**
 * hierarchical config class for tierApiAuthz.server.properties
 * @author mchyzer
 *
 */
public class StandardApiServerConfig extends AsasConfigPropertiesCascadeBase {

  /**
   * use the factory
   */
  private StandardApiServerConfig() {
    
  }
  
  /**
   * retrieve a config from the config file or from cache
   * @return the config object
   */
  public static StandardApiServerConfig retrieveConfig() {
    return retrieveConfig(StandardApiServerConfig.class);
  }

  /**
   * @see ConfigPropertiesCascadeBase#clearCachedCalculatedValues()
   */
  @Override
  public void clearCachedCalculatedValues() {
    
  }

  /**
   * @see AsasConfigPropertiesCascadeBase#getHierarchyConfigKey
   */
  @Override
  protected String getHierarchyConfigKey() {
    return "tierApiAuthzServer.config.hierarchy";
  }

  /**
   * @see AsasConfigPropertiesCascadeBase#getMainConfigClasspath
   */
  @Override
  protected String getMainConfigClasspath() {
    return "tierApiAuthz.server.properties";
  }
  
  /**
   * @see AsasConfigPropertiesCascadeBase#getMainExampleConfigClasspath
   */
  @Override
  protected String getMainExampleConfigClasspath() {
    return "tierApiAuthz.server.base.properties";
  }

  /**
   * @see AsasConfigPropertiesCascadeBase#getSecondsToCheckConfigKey
   */
  @Override
  protected String getSecondsToCheckConfigKey() {
    return "tierApiAuthzServer.config.secondsBetweenUpdateChecks";
  }

  
  
}
