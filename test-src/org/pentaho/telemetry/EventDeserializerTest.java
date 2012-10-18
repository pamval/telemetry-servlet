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

import org.pentaho.telemetry.deserializers.EventDeserializerJson;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class EventDeserializerTest {

  @Test
  public void testDeserializeEvents() throws JSONException {
    String eventsAsJson = "[{" + 
	"\"class\": \"org.pentaho.telemetry.TelemetryEvent\"," + 
	"\"eventTimestamp\": 1350338714856," + 
	"\"eventType\": \"USAGE\"," + 
	"\"extraInfo\": null," + 
	"\"platformVersion\": \"2.0.0\"," + 
	"\"pluginName\": \"marketplace\"," + 
	"\"pluginVersion\": \"TRUNK-TRUNK-SNAPSHOT\"," + 
	"\"urlToCall\": \"http://localhost/pentahoTelemetry\"" + 
"}," + 
"{" + 
	"\"class\": \"org.pentaho.telemetry.TelemetryEvent\"," + 
	"\"eventTimestamp\": 1350338947745," + 
	"\"eventType\": \"INSTALLATION\"," + 
	"\"extraInfo\": {" + 
		"\"installedVersion\": \"TRUNK-SNAPSHOT\"," + 
		"\"installedBranch\": \"TRUNK\"," + 
		"\"installedPlugin\": \"cda\"" + 
	"}," + 
	"\"platformVersion\": \"2.0.0\"," + 
	"\"pluginName\": \"marketplace\"," + 
	"\"pluginVersion\": \"TRUNK-TRUNK-SNAPSHOT\"," + 
	"\"urlToCall\": \"http://localhost/pentahoTelemetry\"" + 
"}]";
 
    JSONArray array = new JSONArray(eventsAsJson);
    
    List<JSONObject> objects = new ArrayList<JSONObject>(array.length());
    for (int i=0; i < array.length();i++) 
        objects.add(array.getJSONObject(i));
    
    EventDeserializerJson deserializer = new EventDeserializerJson();
    List<TelemetryEvent> resultList = deserializer.deserializeEvents(objects, "217.249.10.2");
    
    Assert.assertEquals(2, resultList.size());
    TelemetryEvent te  = resultList.get(0);
    Assert.assertEquals("2.0.0", te.getPlatformVersion());
    Assert.assertEquals("marketplace", te.getPluginName());
    Assert.assertEquals("TRUNK-TRUNK-SNAPSHOT", te.getPluginVersion());
    Assert.assertEquals(TelemetryEvent.TelemetryEventType.USAGE, te.getEventType());
    Assert.assertEquals("217.249.10.2", te.getOrigin());
        
    te  = resultList.get(1);
    Assert.assertEquals(3, te.getExtraInfo().size());
    Assert.assertEquals(TelemetryEvent.TelemetryEventType.INSTALLATION, te.getEventType());
    Assert.assertEquals("TRUNK", te.getExtraInfo().get("installedBranch"));
        
  }
    
  
  
}
