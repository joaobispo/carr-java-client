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
import pt.amaze.ASLCandidates.Interfaces.PropertiesDefinition;
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;

/**
 * Implementation of EnumKey, with the keys to the Preferences of this program
 * and access to a PreferencesEnum.
 *
 * @author Joao Bispo
 */
public enum ClientPreferences implements EnumKey {

   SerialPortName("COM4"),
   ServerAddress("127.0.0.1"),
   ServerPort("7890"),
   FirstReadTimeoutMillis("5000");

   /**
    * Enum Constructor
    * 
    * @param defaultValue
    */
   private ClientPreferences(String defaultValue) {
      this.defaultValue = defaultValue;
   }


   @Override
   public String getKey() {
      return this.name();
   }

   @Override
   public String getDefaultValue() {
      return defaultValue;
   }

   /**
    * 
    * @return PreferencesEnum object associated with this program.
    */
   public static PreferencesEnum getPreferences() {
      if(preferences == null) {
         preferences = initializePreferences();
      }

      return preferences;
   }


   /**
    * Initiallizes the Preferences object:
    *    - Asks for the Preferences associated with this package;
    *    - Looks for a Properties file and if found, loads and stores its
    *  definitions.
    *
    * @return a PreferencesEnum initialized for the ClientModule package.
    */
   private static PreferencesEnum initializePreferences() {
      // Build Properties Definitions
      PropertiesDefinition propsDef = new ClientProperties();

      // Ask for Preferences
      PreferencesEnum newPreferences = new PreferencesEnum(ClientPreferences.class, true);

      /*
      // Find Properties file
      Properties properties = IoUtils.loadProperties(CLIENT_PROPERTIES_FILENAME);
      // Load properties
      if(properties != null) {
         newPreferences.loadProperties(properties);
      }
       */

      return newPreferences;
   }

   /**
    * INSTANCE VARIABLES
    */
   //private static final PreferencesEnum preferences = initializePreferences();
   private static PreferencesEnum preferences = null;

   private final String defaultValue;

   // TODO: Remove
   //private static final String CLIENT_PROPERTIES_FILENAME = "client.properties";
}
