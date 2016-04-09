/*
 * @author mchyzer $Id: GrouperRestServlet.java,v 1.13 2009-11-20 07:15:38 mchyzer Exp $
 */
package edu.internet2.middleware.tierApiAuthzServer.j2ee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.internet2.middleware.tierApiAuthzServer.contentType.AsasRestContentType;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasDefaultResourceContainer;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasDefaultVersionResourceContainer;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasResponseBeanBase;
import edu.internet2.middleware.tierApiAuthzServer.corebeans.AsasResultProblem;
import edu.internet2.middleware.tierApiAuthzServer.exceptions.AsasRestInvalidRequest;
import edu.internet2.middleware.tierApiAuthzServer.rest.AsasRestHttpMethod;
import edu.internet2.middleware.tierApiAuthzServer.util.StandardApiServerConfig;
import edu.internet2.middleware.tierApiAuthzServer.util.StandardApiServerUtils;
import edu.internet2.middleware.tierApiAuthzServer.version.TaasWsVersion;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.logging.Log;
import edu.internet2.middleware.tierApiAuthzServerExt.org.apache.commons.logging.LogFactory;

/**
 * servlet for rest web services
 */
public class TaasRestServlet extends HttpServlet {

  /** logger */
  private static final Log LOG = LogFactory.getLog(TaasRestServlet.class);

  /** when this servlet was started */
  private static long startupTime = System.currentTimeMillis();
  
  /**
   * keep warnings in thread local so they can be accessed from anywhere
   */
  private static ThreadLocal<StringBuilder> threadLocalWarnings = new ThreadLocal<StringBuilder>();

  /**
   * 
   * @return the warnings
   */
  public static StringBuilder threadLocalWarnings() {
    return threadLocalWarnings.get();
  }
  
  /**
   * id
   */
  private static final long serialVersionUID = 1L;

  
  /**
   * @return the startupTime
   */
  public static long getStartupTime() {
    return TaasRestServlet.startupTime;
  }

  /**
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    long servetStarted = System.nanoTime();
    
    TaasFilterJ2ee.assignHttpServlet(this);
    List<String> urlStrings = null;
    StringBuilder warnings = new StringBuilder();
    threadLocalWarnings.set(warnings);

    AsasResponseBeanBase asasResponseBean = null;
    
    //we need something here if errors, so default to xhtml
    AsasRestContentType wsRestContentType = AsasRestContentType.json;
    AsasRestContentType.assignContentType(wsRestContentType);

    boolean indent = false;
    
    try {
      
      if (StandardApiServerUtils.booleanValue(request.getParameter("indent"), false)) {
        indent = true;
      }
      
      //init params (if problem, exception will be thrown)
      request.getParameterMap();
      
      urlStrings = extractUrlStrings(request);
      int urlStringsLength = StandardApiServerUtils.length(urlStrings);

      //get the body and convert to an object
      String body = StandardApiServerUtils.toString(request.getReader());

      TaasWsVersion clientVersion = null;

      //get the method and validate (either from object, or HTTP method
      AsasRestHttpMethod asasRestHttpMethod = null;
      {
        String methodString = request.getMethod();
        asasRestHttpMethod = AsasRestHttpMethod.valueOfIgnoreCase(methodString, true);
      }
      
      //if there are other content types, detect them here
      boolean foundContentType = false;
      
      //we are always json
      if (request.getRequestURI().endsWith(".json") || true) {
        wsRestContentType = AsasRestContentType.json;
        foundContentType = true;
      }
      AsasRestContentType.assignContentType(wsRestContentType);

//   we could strip off extension if we had different types
//      if (foundContentType && urlStringsLength > 0) {
//        
//        String lastUrlString = urlStrings.get(urlStringsLength-1);
//        if (lastUrlString.endsWith("." + wsRestContentType.name())) {
//          lastUrlString = lastUrlString.substring(0, lastUrlString.length()-(1+wsRestContentType.name().length()));
//        }
//        urlStrings.set(urlStringsLength-1, lastUrlString);
//      }
      
      if (urlStringsLength == 0) {
        
        if (asasRestHttpMethod != AsasRestHttpMethod.GET) {
          throw new AsasRestInvalidRequest("Cant have non-GET method for default resource: " + asasRestHttpMethod);
        }
        
        if (foundContentType) {
          
          asasResponseBean = new AsasDefaultVersionResourceContainer();
          
        } else {
          asasResponseBean = new AsasDefaultResourceContainer();
        }
      } else {
        
        if (!foundContentType) {
          throw new AsasRestInvalidRequest("Request must end in .json or .xml: " + request.getRequestURI());
        }
        
        //first see if version
        clientVersion = TaasWsVersion.valueOfIgnoreCase(StandardApiServerUtils.popUrlString(urlStrings), true);

        TaasWsVersion.assignCurrentClientVersion(clientVersion, warnings);
        
  //      WsRequestBean requestObject = null;
  //
  //      if (!StringUtils.isBlank(body)) {
  //        requestObject = (WsRequestBean) wsRestRequestContentType.parseString(body,
  //            warnings);
  //      }
  //      
  //      //might be in params (which might not be in body
  //      if (requestObject == null) {
  //        //might be in http params...
  //        requestObject = (WsRequestBean) GrouperServiceUtils.marshalHttpParamsToObject(
  //            request.getParameterMap(), request, warnings);
  //
  //      }
                    
        asasResponseBean = asasRestHttpMethod.service(urlStrings, request.getParameterMap(), body);
      }
    } catch (AsasRestInvalidRequest arir) {

      asasResponseBean = new AsasResultProblem();
      String error = arir.getMessage() + ", " + requestDebugInfo(request);

      //this is a user error, but an error nonetheless
      LOG.error(error, arir);

      asasResponseBean.setError_description(error + StandardApiServerUtils.getFullStackTrace(arir));
      asasResponseBean.getMeta().setSuccess(false);
      asasResponseBean.getMeta().setStatus("INVALID_QUERY");
      asasResponseBean.setError("INVALID_QUERY");
      asasResponseBean.getResponseMeta().setHttpStatusCode(400);

    } catch (RuntimeException e) {

      //this is not a user error, is a big problem

      asasResponseBean = new AsasResultProblem();
      LOG.error("Problem with request: " + requestDebugInfo(request), e);
      asasResponseBean.setError_description("Problem with request: "
          + requestDebugInfo(request) + ",\n" + StandardApiServerUtils.getFullStackTrace(e));
      asasResponseBean.getMeta().setSuccess(false);
      asasResponseBean.getMeta().setStatus("EXCEPTION");
      asasResponseBean.setError("ERROR");
      asasResponseBean.getResponseMeta().setHttpStatusCode(500);

    }
    
    //set http status code, content type, and write the response
    try {
      { 
        StringBuilder urlBuilder = new StringBuilder();
        {
          String url = request.getRequestURL().toString();
          url = StandardApiServerUtils.prefixOrSuffix(url, "?", true);
          urlBuilder.append(url);
        }
        //lets put the params back on (the ones we expect)
        Map<String, String> paramMap = request.getParameterMap();
        boolean firstParam = true;
        for (String paramName : paramMap.keySet()) {
          if (firstParam) {
            urlBuilder.append("?");
          } else {
            urlBuilder.append("&");
          }
          firstParam = false;
          
          urlBuilder.append(StandardApiServerUtils.escapeUrlEncode(paramName))
            .append("=").append(StandardApiServerUtils.escapeUrlEncode(paramMap.get(paramName)));
          
        }
        String selfUri = urlBuilder.toString();
        if (selfUri.startsWith(StandardApiServerUtils.servletUrl() + "/")) {
          selfUri = selfUri.substring(StandardApiServerUtils.servletUrl().length());
        }
        asasResponseBean.getMeta().setSelfUri(selfUri);
      }
      if (warnings.length() > 0) {
        asasResponseBean.getMeta().appendWarning(warnings.toString());
      }

      {
        Set<String> unusedParams = ((AsasHttpServletRequest)request).unusedParams();
        //add warnings about unused params
        if (StandardApiServerUtils.length(unusedParams) > 0) {
          for (String unusedParam : unusedParams) {
            asasResponseBean.getMeta().appendWarning("Unused HTTP param: " + unusedParam);
          }
        }
      }     
      
      //structure name
      asasResponseBean.getMeta().setStructureName(StandardApiServerUtils.structureName(asasResponseBean.getClass()));
      
      //headers should be there by now
      //set the status code
      response.setStatus(asasResponseBean.getResponseMeta().getHttpStatusCode());

      String restCharset = StandardApiServerConfig.retrieveConfig().propertyValueString("tierApiAuthzServer.restHttpContentTypeCharset");
      String responseContentType = wsRestContentType.getContentType();
      
      if (!StandardApiServerUtils.isBlank(restCharset)) {
        responseContentType += "; charset=" + restCharset;
      }
      
      response.setContentType(responseContentType);

      //temporarily set to uuid, so we can time the content generation
      long millisUuid = -314253647586987L;
      
      asasResponseBean.getResponseMeta().setMillis(millisUuid);
      
      String responseString = wsRestContentType.writeString(asasResponseBean);
      
      if (indent) {
        responseString = wsRestContentType.indent(responseString);
      }
      
      responseString = StandardApiServerUtils.replace(responseString, Long.toString(millisUuid), Long.toString(((System.nanoTime()-servetStarted) / 1000000)));
      
      try {
        response.getWriter().write(responseString);
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
      
    } catch (RuntimeException re) {
      //problem!
      LOG.error("Problem with request: " + requestDebugInfo(request), re);
    } finally {

      StandardApiServerUtils.closeQuietly(response.getWriter());
      TaasWsVersion.removeCurrentClientVersion();
      AsasRestContentType.clearContentType();

    }
    
    HttpSession httpSession = request.getSession(false);
    if (httpSession != null) {
      httpSession.invalidate();
    }
    threadLocalWarnings.remove();
  }

  /**
   * for error messages, get a detailed report of the request
   * @param request
   * @return the string of descriptive result
   */
  public static String requestDebugInfo(HttpServletRequest request) {
    StringBuilder result = new StringBuilder();
    result.append(" uri: ").append(request.getRequestURI());
    result.append(", HTTP method: ").append(((AsasHttpServletRequest)request).getOriginalMethod());
    if (!StandardApiServerUtils.isBlank(request.getParameter("method"))) {
      result.append(", HTTP param method: ").append(request.getParameter("method"));
    }
    result.append(", decoded url strings: ");
    List<String> urlStrings = extractUrlStrings(request);
    int urlStringsLength = StandardApiServerUtils.length(urlStrings);
    if (urlStringsLength == 0) {
      result.append("[none]");
    } else {
      for (int i = 0; i < urlStringsLength; i++) {
        result.append(i).append(": '").append(urlStrings.get(i)).append("'");
        if (i != urlStringsLength - 1) {
          result.append(", ");
        }
      }
    }
    return result.toString();
  }

  /**
   * take a request and get the list of url strings for the rest web service
   * @see #extractUrlStrings(String)
   * @param request is the request to get the url strings out of
   * @return the list of url strings
   */
  private static List<String> extractUrlStrings(HttpServletRequest request) {
    String requestResourceFull = request.getRequestURI();
    return extractUrlStrings(requestResourceFull);
  }

  /**
   * <pre>
   * take a request uri and break up the url strings not including the app name or servlet
   * this does not include the url params (if applicable)
   * if the input is: grouper-ws/servicesRest/xhtml/v1_3_000/groups/members
   * then the result is a list of size 2: {"group", "members"}
   * 
   * </pre>
   * @param requestResourceFull
   * @return the url strings
   */
  private static List<String> extractUrlStrings(String requestResourceFull) {
    String[] requestResources = StandardApiServerUtils.split(requestResourceFull, '/');
    List<String> urlStrings = new ArrayList<String>();

    //loop through and decode
    int index = 0;
    for (String requestResource : requestResources) {
      //skip the app name and lite servlet
      if (index++ < 2) {
        continue;
      }
      //unescape the url encoding
      urlStrings.add(StandardApiServerUtils.escapeUrlDecode(requestResource));
    }
    return urlStrings;
  }

}
