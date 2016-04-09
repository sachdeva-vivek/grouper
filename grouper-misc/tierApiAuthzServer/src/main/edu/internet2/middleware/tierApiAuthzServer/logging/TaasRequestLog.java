/**
 * @author mchyzer
 * $Id$
 */
package edu.internet2.middleware.tierApiAuthzServer.logging;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import edu.internet2.middleware.tierApiAuthzServer.j2ee.TaasFilterJ2ee;
import edu.internet2.middleware.tierApiAuthzServer.util.StandardApiServerUtils;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.logging.Log;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.logging.LogFactory;


/**
 *
 */
public class TaasRequestLog {

  /** logger */
  private static final Log LOG = LogFactory.getLog(TaasRequestLog.class);

  /**
   * log item to request log
   * @param label
   * @param object
   */
  public static void logToRequestLog(String label, Object object) {
    
    HttpServletRequest httpServletRequest = TaasFilterJ2ee.retrieveHttpServletRequest();
    
    if (httpServletRequest == null) {
      LOG.error("Why is request null??? " + label + ": " + StandardApiServerUtils.toStringForLog(object, 1000), 
          new RuntimeException("Not a real exception, just getting the stack"));
      return;
    }
    
    Map<String, Object> logMap = requestLog(httpServletRequest);

    //make reasonable attempts to avoid collisions
    if (logMap.containsKey(label)) {
      for (int i=0; i<100; i++) {
        String newLabel = label + "_" + i;
        if (logMap.containsKey(newLabel)) {
          continue;
        }
        label = newLabel;
        break;
      }
    }
    
    logMap.put(label, object);
  }

  /**
   * @param httpServletRequest
   * @return the map
   */
  private static Map<String, Object> requestLog(HttpServletRequest httpServletRequest) {
    
    String requestLogAttributeName = "tierRequestLogMap";

    Map<String, Object> logMap = (Map<String, Object>)httpServletRequest.getAttribute(requestLogAttributeName);

    if (logMap == null) {
      logMap = new LinkedHashMap<String, Object>();
      httpServletRequest.setAttribute(requestLogAttributeName, logMap);
    }
    return logMap;
  }
  
  /**
   * 
   */
  public TaasRequestLog() {
  }

  /**
   * log something to the log file
   */
  public static void logRequest() {
    if (LOG.isDebugEnabled()) {
      
      String requestLog = requestLogString();
      if (requestLog != null) {
        LOG.debug(requestLog);
      }
    }

  }

  /**
   * get the current request log string
   * @return the string
   */
  public static String requestLogString() {
    HttpServletRequest httpServletRequest = TaasFilterJ2ee.retrieveHttpServletRequest();
    
    if (httpServletRequest == null) {
      LOG.error("Why is request null??? ", 
          new RuntimeException("Not a real exception, just getting the stack"));
      return null;
    }

    Map<String, Object> logMap = requestLog(httpServletRequest);
    
    return StandardApiServerUtils.mapToString(logMap);
    
  }
}
