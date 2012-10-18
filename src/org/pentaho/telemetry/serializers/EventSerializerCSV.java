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
package org.pentaho.telemetry.serializers;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.telemetry.EventSerializer;
import org.pentaho.telemetry.TelemetryEvent;

public class EventSerializerCSV implements EventSerializer {

  private static Log logger = LogFactory.getLog(EventSerializerCSV.class);
  
  protected String path;
  
  protected static final String CSV_SEPARATOR = ",";
  
  @Override
  public boolean setup(Map<String, String> parameters) {
    //Get path to write files to from parameters
    if (!parameters.containsKey("csvFilePath")) {
      logger.error("Parameters must contain csvFilePath with path where to save the files to");
      return false;
    }
    this.path = parameters.get("csvFilePath");
    return true;
  }

  @Override
  public boolean serializeEvents(List<TelemetryEvent> events) {
    //Create a new file
    String fileName = getFileName();
    BufferedWriter bw = null;
    try {
      bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
    } catch (FileNotFoundException ex) {
      logger.error("Unable to open file for writing. FileName: " + fileName, ex);
      return false;
    } catch (UnsupportedEncodingException ex) {
      logger.error("Unsupported encoding UTF-8?", ex);
      return false;
    }

    StringBuilder line = new StringBuilder();
    
    line.append("pluginName").append(CSV_SEPARATOR)
            .append("pluginVersion").append(CSV_SEPARATOR)
            .append("platformVersion").append(CSV_SEPARATOR)
            .append("timestamp").append(CSV_SEPARATOR)
            .append("type").append(CSV_SEPARATOR)
            .append("origin").append(CSV_SEPARATOR)
            .append("extraInfo");
          
    
    try {
      bw.append(line.toString());
      bw.newLine();
                   
      for (TelemetryEvent te : events) {
        line.delete(0, line.length());
      
        line.append(safeCsvString(te.getPluginName())).append(CSV_SEPARATOR)
                .append(safeCsvString(te.getPluginVersion())).append(CSV_SEPARATOR)
                .append(safeCsvString(te.getPlatformVersion())).append(CSV_SEPARATOR)
                .append(te.getEventTimestamp()).append(CSV_SEPARATOR)
                .append(te.getEventType().name()).append(CSV_SEPARATOR)
                .append(safeCsvString(te.getOrigin())).append(CSV_SEPARATOR)
                .append(safeCsvString(getExtraInfoAsString(te.getExtraInfo())));
        
        bw.append(line.toString());
        bw.newLine();        
      }
      
      bw.flush();
      bw.close();
    } catch (IOException ioe) {
      logger.error("Unable to save csv file with telemetry events", ioe);
      return false;
    }
    
    return true;
  }
 
  
  protected String getExtraInfoAsString(Map<String, String> extraInfo) {
    if (extraInfo == null) return "";
    StringBuilder extraInfoStr = new StringBuilder();
    Iterator<String> keyIterator = extraInfo.keySet().iterator();
    while (keyIterator.hasNext()) {
      String key = keyIterator.next();
      String value = extraInfo.get(key);
      
      extraInfoStr.append(key.replace(";", " ").replace("=", " ")).
              append(" = ").
              append(value.replace(";", " ").replace("=", " ")).
              append("; ");      
    }
    return extraInfoStr.toString();
  }
  
  
  protected String getFileName() {
    return this.path + (path.endsWith("/") ? "": "/") + 
            UUID.randomUUID().toString() + "." + new Date().getTime() + ".csv";
  }
  
  private String safeCsvString(String original) {
    return original.replace(CSV_SEPARATOR, " ").replace("\n", " ").replace("\r", " ");
  }
  
}
