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
package org.pentaho.telemetry;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Test;
import org.pentaho.telemetry.serializers.EventSerializerCSV;

public class EventSerializerTest {

  @After
  public void cleanup() {
    File f = new File("test.csv");
    f.delete();
  }
  
  @Test
  public void testSerializeEvent() throws FileNotFoundException, IOException {
 
    List<TelemetryEvent> resultList = new ArrayList<TelemetryEvent>(1);
    TelemetryEvent te = new TelemetryEvent("plugin", "v1.0", "v2.0", 9080, TelemetryEvent.TelemetryEventType.OTHER, null, "myOrigin");
    resultList.add(te);
    
    EventSerializerCSVForTests serializer = new EventSerializerCSVForTests();
    
    Assert.assertTrue(serializer.serializeEvents(resultList));
    
    FileInputStream fis = new FileInputStream(new File("test.csv"));

    String csvContent = IOUtils.toString(fis, "UTF-8");
    
    String eol =  System.getProperty("line.separator");
    String sep = serializer.getSeparator();
    Assert.assertTrue(csvContent.startsWith("pluginName" + sep + 
            "pluginVersion" + sep + "platformVersion" + sep + "timestamp" + sep 
            + "type" + sep + "origin" + sep + "extraInfo" + eol ));

    Assert.assertTrue(csvContent.endsWith("plugin" + sep + "v1.0" + sep + "v2.0" 
            + sep + "9080" + sep + "OTHER" + sep + "myOrigin" + sep + "" + eol));
    
  }
    

  
  @Test
  public void testSerializeEventWithExtraInfo() throws FileNotFoundException, IOException {
 
    List<TelemetryEvent> resultList = new ArrayList<TelemetryEvent>(1);
    Map<String, String> eInfo = new HashMap<String, String>();
    eInfo.put("t1", "v1");
    eInfo.put("t2", "v2");
    TelemetryEvent te = new TelemetryEvent("plugin", "v1.0", "v2.0", 9080, TelemetryEvent.TelemetryEventType.OTHER, eInfo, "myOrigin");
    resultList.add(te);
    
    EventSerializerCSVForTests serializer = new EventSerializerCSVForTests();
    
    Assert.assertTrue(serializer.serializeEvents(resultList));
    
    FileInputStream fis = new FileInputStream(new File("test.csv"));

    String csvContent = IOUtils.toString(fis, "UTF-8");
    
    String eol =  System.getProperty("line.separator");
    String sep = serializer.getSeparator();
    Assert.assertTrue(csvContent.startsWith("pluginName" + sep + 
            "pluginVersion" + sep + "platformVersion" + sep + "timestamp" + sep 
            + "type" + sep + "origin" + sep + "extraInfo" + eol ));

    Assert.assertTrue(csvContent.contains("plugin" + sep + "v1.0" + sep + "v2.0" 
            + sep + "9080" + sep + "OTHER" + sep + "myOrigin" + sep));
    Assert.assertTrue(csvContent.contains("t1 = v1; "));
    Assert.assertTrue(csvContent.contains("t2 = v2; "));
    
  }
  

  @Test
  public void testSerializeEventWithExtraInfoProtectedChars() throws FileNotFoundException, IOException {
 
    List<TelemetryEvent> resultList = new ArrayList<TelemetryEvent>(1);
    Map<String, String> eInfo = new HashMap<String, String>();
    eInfo.put("t=1", "v1");
    eInfo.put("t2", "v;2");
    TelemetryEvent te = new TelemetryEvent("plugin", "v1.0", "v2.0", 9080, TelemetryEvent.TelemetryEventType.OTHER, eInfo, "myOrigin");
    resultList.add(te);
    
    EventSerializerCSVForTests serializer = new EventSerializerCSVForTests();
    
    Assert.assertTrue(serializer.serializeEvents(resultList));
    
    FileInputStream fis = new FileInputStream(new File("test.csv"));

    String csvContent = IOUtils.toString(fis, "UTF-8");
    
    String eol =  System.getProperty("line.separator");
    String sep = serializer.getSeparator();
    Assert.assertTrue(csvContent.startsWith("pluginName" + sep + 
            "pluginVersion" + sep + "platformVersion" + sep + "timestamp" + sep 
            + "type" + sep + "origin" + sep + "extraInfo" + eol ));

    Assert.assertTrue(csvContent.contains("plugin" + sep + "v1.0" + sep + "v2.0" 
            + sep + "9080" + sep + "OTHER" + sep + "myOrigin" + sep));
    Assert.assertTrue(csvContent.contains("t 1 = v1; "));
    Assert.assertTrue(csvContent.contains("t2 = v 2; "));
    
  }
  
  
  @Test
  public void testSerializeEventNewLineAndSeparator() throws FileNotFoundException, IOException {
 
    
    String eol =  System.getProperty("line.separator");
    List<TelemetryEvent> resultList = new ArrayList<TelemetryEvent>(1);
    TelemetryEvent te = new TelemetryEvent("plu" + eol + "gin", "v1,0", "v2.0", 9080, TelemetryEvent.TelemetryEventType.OTHER, null, "myOrigin");
    resultList.add(te);
    
    EventSerializerCSVForTests serializer = new EventSerializerCSVForTests();
    
    Assert.assertTrue(serializer.serializeEvents(resultList));
    
    FileInputStream fis = new FileInputStream(new File("test.csv"));

    String csvContent = IOUtils.toString(fis, "UTF-8");
    
   
    String sep = serializer.getSeparator();
    Assert.assertTrue(csvContent.startsWith("pluginName" + sep + 
            "pluginVersion" + sep + "platformVersion" + sep + "timestamp" + sep 
            + "type" + sep + "origin" + sep + "extraInfo" + eol ));

    Assert.assertTrue(csvContent.endsWith("plu gin" + sep + "v1 0" + sep + "v2.0" 
            + sep + "9080" + sep + "OTHER" + sep + "myOrigin" + sep + "" + eol));
    
  }
  
  
  @Test
  public void testGetFileName() {
    EventSerializerCSVForTests serializer = new EventSerializerCSVForTests();
    Map<String, String> confParameters = new HashMap<String, String>();
    confParameters.put("csvFilePath", "/opt/pentaho");
    
    Assert.assertTrue(serializer.setup(confParameters));
    
    String file = serializer.getBaseFileName();
    
    Assert.assertTrue(file.startsWith("/opt/pentaho/"));
    
    Assert.assertTrue(file.endsWith(".csv"));
    
  }
  
  
  class EventSerializerCSVForTests extends EventSerializerCSV {
    
    public String getBaseFileName() {
      return super.getFileName();
    }
    
    public String getSeparator() {
      return CSV_SEPARATOR;
    }
    
    @Override 
    protected String getFileName() {
      return "test.csv";
      
    }
  }
  
  
}
