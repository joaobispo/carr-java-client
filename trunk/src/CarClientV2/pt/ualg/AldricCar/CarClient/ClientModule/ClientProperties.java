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

package pt.ualg.AldricCar.CarClient.ClientModule;

import pt.amaze.ASLCandidates.Interfaces.EnumKey;
import pt.amaze.ASLCandidates.Preferences.PropertiesDefinition;

/**
 * Contains information about the Client’s Properties file.
 *
 * @author Joao Bispo
 */
public class ClientProperties extends PropertiesDefinition {

   public ClientProperties() {
      super(ClientPreferences.values().length);
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
      addSection(" Server Address", ClientPreferences.ServerAddress);
      addSection("");
      addSection(" Server Port", ClientPreferences.ServerPort);
     
      // CarpadController Part
      addSection("");
      addSection("");
      addSection("#");
      addSection(" Carpad Controller");
      addSection("#");
      addSection("");
      addSection(" Carpad Timeout For the First Message in Milliseconds", ClientPreferences.FirstReadTimeoutMillis);
      addSection("");
      addSection(" Carpad Controller Serial Port Name", ClientPreferences.SerialPortName);
     
   }


   public EnumKey valueOf(String keyName) {
      EnumKey parameter;
      try {
         parameter = ClientPreferences.valueOf(keyName);
      } catch (IllegalArgumentException ex) {
         parameter = null;
      }
      return parameter;
   }


   public String getPropertiesFilename() {
      return CLIENT_PROPERTIES_FILENAME;
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final String CLIENT_PROPERTIES_FILENAME = "client.properties";

}
