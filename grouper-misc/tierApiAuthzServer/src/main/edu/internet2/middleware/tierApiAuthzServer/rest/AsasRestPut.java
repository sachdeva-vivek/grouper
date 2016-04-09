package edu.internet2.middleware.tierApiAuthzServer.rest;

import java.util.List;
import java.util.Map;

import edu.internet2.middleware.tierApiAuthzServer.contentType.AsasRestContentType;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasFolderSaveRequest;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasResponseBeanBase;
import edu.internet2.middleware.tierApiAuthzServer.exceptions.AsasRestInvalidRequest;
import edu.internet2.middleware.tierApiAuthzServer.j2ee.TaasRestServlet;
import edu.internet2.middleware.tierApiAuthzServer.util.StandardApiServerUtils;


public enum AsasRestPut {

  /** folder put requests */
  folders {

    /**
     * handle the incoming request based on PUT HTTP method and folders resource
     * @param urlStrings not including the app name or servlet.  
     * for http://localhost/tierApiAuthz/tierApiAuthz/v1/folders/id:123.json
     * @param requestObject is the request body converted to object
     * @return the result object
     */
    @Override
    public AsasResponseBeanBase service(List<String> urlStrings,
        Map<String, String> params, String body) {

      AsasFolderSaveRequest asasFolderSaveRequest = null;
      if (!StandardApiServerUtils.isBlank(body)) {
        AsasRestContentType asasRestContentType = AsasRestContentType.retrieveContentType();
        asasFolderSaveRequest = asasRestContentType.parseString(AsasFolderSaveRequest.class, body, 
            TaasRestServlet.threadLocalWarnings());
      }

      if (StandardApiServerUtils.length(urlStrings) != 1) {
        throw new AsasRestInvalidRequest("Expecting 1 url string after 'folders': " 
            + StandardApiServerUtils.toStringForLog(urlStrings));
      }
      
      String folderUri = StandardApiServerUtils.popUrlString(urlStrings);
      
      return AsasRestLogic.folderSave(asasFolderSaveRequest, folderUri, params, true);
    }
  };

  /**
   * do a case-insensitive matching
   * 
   * @param string
   * @param exceptionOnNotFound true if exception should be thrown on not found
   * @return the enum or null or exception if not found
   * @throws GrouperRestInvalidRequest if there is a problem
   */
  public static AsasRestPut valueOfIgnoreCase(String string,
      boolean exceptionOnNotFound) throws AsasRestInvalidRequest {
    return StandardApiServerUtils.enumValueOfIgnoreCase(AsasRestPut.class, 
        string, exceptionOnNotFound);
  }

  /**
   * handle the incoming request based on HTTP method
   * @param clientVersion version of client, e.g. v1_3_000
   * @param urlStrings not including the app name or servlet.  for http://localhost/grouper-ws/servicesRest/groups/a:b
   * the urlStrings would be size two: {"group", "a:b"}
   * @param requestObject is the request body converted to object
   * @return the result object
   */
  public abstract AsasResponseBeanBase service(
      List<String> urlStrings,
      Map<String, String> params, String body);

}
