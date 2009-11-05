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
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;

/**
 * Implementation of EnumKey, with the keys to the Preferences of this program
 * and access to a PreferencesEnum.
 *
 * @author Joao Bispo
 */
public enum CorePreferences implements EnumKey {

   SerialPortName("COM4");

   /**
    * Enum Constructor
    * 
    * @param defaultValue
    */
   private CorePreferences(String defaultValue) {
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
      return preferences;
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final PreferencesEnum preferences =
           new PreferencesEnum(CorePreferences.class, true);
   private final String defaultValue;
}
