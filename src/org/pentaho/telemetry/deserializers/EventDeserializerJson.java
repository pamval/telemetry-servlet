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
 * Created Oct 17th, 2012
 * @author Pedro Vale (pedro.vale@webdetails.pt)
 */
package org.pentaho.telemetry.deserializers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.pentaho.telemetry.EventDeserializer;
import org.pentaho.telemetry.TelemetryEvent;

public class EventDeserializerJson implements EventDeserializer<JSONObject> {

  private static Log logger = LogFactory.getLog(EventDeserializerJson.class);
  
  
  @Override
  public List<TelemetryEvent> deserializeEvents(List<JSONObject> serializedEvents, String origin) {
    List<TelemetryEvent> result = new ArrayList<TelemetryEvent>(serializedEvents.size());
    for (JSONObject obj : serializedEvents) {
      try {
      String pluginName = obj.optString("pluginName");
      String pluginVersion = obj.optString("pluginVersion");
      String platformVersion = obj.optString("platformVersion");
      long timestamp = obj.optLong("eventTimestamp");
      String eventType = obj.optString("eventType");
      TelemetryEvent.TelemetryEventType t = TelemetryEvent.TelemetryEventType.OTHER;
      if ("INSTALLATION".equals(eventType)) {
        t = TelemetryEvent.TelemetryEventType.INSTALLATION;
      } else if ("REMOVAL".equals(eventType)) {
        t = TelemetryEvent.TelemetryEventType.REMOVAL;
      } else if ("USAGE".equals(eventType)) {
        t = TelemetryEvent.TelemetryEventType.USAGE;
      } 

      Map<String, String> extraInfo = new HashMap<String, String>();
      JSONObject infoObject = obj.optJSONObject("extraInfo");
      if (infoObject != null) {
        Iterator infoKeysIterator = infoObject.keys();
        while (infoKeysIterator.hasNext()) {
          String key = (String)infoKeysIterator.next();
          String value = infoObject.getString(key);
          extraInfo.put(key, value);
        }
      }
      TelemetryEvent te = new TelemetryEvent(pluginName, pluginVersion, platformVersion, timestamp, t, extraInfo, origin);
      result.add(te);
      } catch (JSONException e ) {
        String eventAsString = "";
        try {
          eventAsString = obj.toString(2);
        } catch (JSONException jse) {
          logger.error("Unable to deserialize event as string", jse);
        }
        logger.error("Error deserializing event: " + eventAsString + ". Ignoring and continuing", e);
      }
    }
    
    return result;
  }

}
