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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.telemetry.TelemetryEvent;

import java.util.*;


public class DeferredEventSerializerCSV extends EventSerializerCSV {

  private static Log logger = LogFactory.getLog( DeferredEventSerializerCSV.class );
  private Timer t;
  private List<TelemetryEvent> keptEvents;
  protected int interval;


  @Override
  public boolean shutdown() {
    synchronized ( keptEvents ) {
      return serializeEventsToFile( getFileName(), keptEvents );
    }
  }

  @Override
  public boolean setup( Map<String, String> parameters ) {
    keptEvents = new ArrayList<TelemetryEvent>();
    if ( super.setup( parameters ) ) {
      interval = 1000 * 60 * 60; //Default interval to write is 60 minutes
      if ( parameters.containsKey( "writeInterval" ) ) {
        interval = Integer.parseInt( parameters.get("writeInterval") ) * 1000;
      }
      t = new Timer( "EventWriter", true );
      t.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          synchronized ( keptEvents ) {
            if ( keptEvents.size() > 0 && serializeEventsToFile( getFileName(), keptEvents ) )
              keptEvents.clear();
            else
              logger.warn( "Unable to serialize events to file. Will try again in next run" );
          }
        }
      }, interval, interval );
      return true;
    } else {
      return false;
    }
  }


  @Override
  public boolean serializeEvents( List<TelemetryEvent> events ) {
    synchronized ( keptEvents ) {
      keptEvents.addAll( events );
    }
    return true;
  }
 
  

}
