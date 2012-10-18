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
package org.pentaho.telemetry;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

 
 /**
  * Represents a telemetry event. 
  * @author pedrovale
  */
public class TelemetryEvent implements Serializable {

  /**
   * @return the origin
   */
  public String getOrigin() {
    return origin;
  }

  /**
   * @param origin the origin to set
   */
  public void setOrigin(String origin) {
    this.origin = origin;
  }

 public enum TelemetryEventType {

    INSTALLATION, REMOVAL, USAGE, OTHER
  };  
  
  private String origin;
  private String pluginName, 
          pluginVersion,
          platformVersion;
  private long eventTimestamp;
  private TelemetryEventType eventType;

  private Map<String, String> extraInfo;
  
  
  public TelemetryEvent(String pluginName, String pluginVersion, String platformVersion, long timestamp, 
          TelemetryEventType eventType, Map<String, String> extraInfo, String origin) {
    this.pluginName = pluginName;
    this.pluginVersion = pluginVersion;
    this.platformVersion = platformVersion;
    this.eventTimestamp = timestamp;
    this.eventType = eventType;
    this.extraInfo = extraInfo;
    this.origin = origin;
  }
  

  /**
   * @return the pluginName
   */
  public String getPluginName() {
    return pluginName;
  }

  /**
   * @return the pluginVersion
   */
  public String getPluginVersion() {
    return pluginVersion;
  }

  /**
   * @return the platformVersion
   */
  public String getPlatformVersion() {
    return platformVersion;
  }

  /**
   * @return the eventTimestamp
   */
  public long getEventTimestamp() {
    return eventTimestamp;
  }

  /**
   * @return the eventType
   */
  public TelemetryEventType getEventType() {
    return eventType;
  }

  /**
   * @return the extraInfo
   */
  public Map<String, String> getExtraInfo() {
    return extraInfo;
  }
  
}
