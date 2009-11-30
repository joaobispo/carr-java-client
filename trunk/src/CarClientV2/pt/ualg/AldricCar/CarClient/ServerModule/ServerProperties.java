/*
 *  Copyright 2009 Abstract Maze.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package pt.ualg.AldricCar.CarClient.ServerModule;

import pt.amaze.ASLCandidates.Interfaces.EnumKey;
import pt.amaze.ASLCandidates.Preferences.PropertiesDefinition;

/**
 * Contains information about the Client’s Properties file.
 *
 * @author Joao Bispo
 */
public class ServerProperties extends PropertiesDefinition {

   public ServerProperties() {
      super(ServerPreferences.values().length);
   }

   protected void buildSections() {
      
      // Initial Header
      addSection("#");
      addSection("Properties file for Aldric's Car Client");
      addSection("#");
      addSection("");

      // Server Part
      addSection("#");
      addSection(" Server");
      addSection("#");
      addSection("");
      addSection(" Server Port", ServerPreferences.ServerPort);
      addSection("");
      addSection(" Information Output Interval in Millis", ServerPreferences.InfoIntervalMillis);
     
   }


   public EnumKey valueOf(String keyName) {
      EnumKey parameter;
      try {
         parameter = ServerPreferences.valueOf(keyName);
      } catch (IllegalArgumentException ex) {
         parameter = null;
      }
      return parameter;
   }


   public String getPropertiesFilename() {
      return SERVER_PROPERTIES_FILENAME;
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final String SERVER_PROPERTIES_FILENAME = "server.properties";

}
