/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *
 * Created Oct 18th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */
package org.pentaho.telemetry.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.telemetry.EventDeserializer;
import org.pentaho.telemetry.EventSerializer;
import org.pentaho.telemetry.deserializers.EventDeserializerJson;
import org.pentaho.telemetry.serializers.EventSerializerCSV;

public class TelemetryServlet extends HttpServlet {
  
  private static Log logger = LogFactory.getLog(EventSerializerCSV.class);
  
  
  private EventSerializer serializer = new EventSerializerCSV();
  private EventDeserializer deserializer = new EventDeserializerJson();
  
  /**
   * Initializes the servlet.
   */
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    Enumeration initParameterNames = config.getInitParameterNames();
    Map<String, String> params = new HashMap<String, String>();
    while (initParameterNames.hasMoreElements()) {
      String name = (String) initParameterNames.nextElement();
      String value = config.getInitParameter(name);
      params.put(name, value);
    }

    serializer.setup(params);
  }

  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void doGet(
          HttpServletRequest request, HttpServletResponse response)
          throws ServletException, java.io.IOException {
    response.getOutputStream().print("<ERROR>Servlet only answers to POST requests</ERROR>");
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   */
  protected void doPost(
          HttpServletRequest request, HttpServletResponse response)
          throws ServletException, java.io.IOException {
    
    String body = getBody(request);
    
    logger.debug("Got body " + body);
    
    body = body.substring(body.indexOf("[") ,body.lastIndexOf("]")+1);
    String origin = getClientIpAddr(request);
    JSONArray array = null;
    try {
      array = new JSONArray(URLDecoder.decode(body));
    } catch (JSONException jse) {
      logger.error("Unable to parse body string as json: " + body, jse);
      response.getOutputStream().print("<ERROR>Unable to parse body as JSON</ERROR>");
      return;
    }
    
    List<JSONObject> objects = new ArrayList<JSONObject>(array.length());
    for (int i = 0; i < array.length(); i++) {
        try {
          objects.add(array.getJSONObject(i));
        } catch (JSONException jse) {
          logger.error("Unable to parse element " + i + " in body " + body + ". Ignoring", jse);
        }      
    }
    
    if (serializer.serializeEvents(deserializer.deserializeEvents(objects, origin))) {
      logger.info("Events saved!");
      response.getOutputStream().print("<result>OK</result>");
    } else {
      logger.error("Could not save list of events " + body);
      response.getOutputStream().print("<ERROR>Could not save events</ERROR>");
    }
  }
  
  
  private static String getBody(HttpServletRequest request) throws IOException {
    StringBuilder stringBuilder = new StringBuilder();
   
    try {
        byte[] buffer = new byte[512];
        int bytesRead = 0;
        while ((bytesRead = request.getInputStream().read(buffer)) > 0) {          
          stringBuilder.append(new String(buffer,0,bytesRead));
        }
    } catch (IOException ex) {
      throw ex;
    }
    return stringBuilder.toString();
  }
  
    public static String getClientIpAddr(HttpServletRequest request) {  
            String ip = request.getHeader("X-Forwarded-For");  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("Proxy-Client-IP");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("WL-Proxy-Client-IP");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_CLIENT_IP");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
            }  
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
                ip = request.getRemoteAddr();  
            }  
            return ip;  
        }    
  
}
