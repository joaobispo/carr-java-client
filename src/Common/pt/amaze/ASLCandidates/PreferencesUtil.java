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

package pt.amaze.ASLCandidates;

import java.util.List;
import java.util.prefs.Preferences;
import pt.amaze.ASLCandidates.Interfaces.PropertiesDefinition;
import pt.amaze.ASLCandidates.Interfaces.PropertiesDefinition.Section;
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;

/**
 * Utility methods for Preferences-related classes.
 *
 * @author Joao Bispo
 */
public class PreferencesUtil {

   /**
    * Builds a String with represents a Properties file built from the 
    * information in PropertiesDefinition and the data in Preferences.
    * 
    * @param propertiesDef
    * @param preferences
    * @return
    */
   public static String generateProperties(PropertiesDefinition propertiesDef, Preferences preferences) {
      int builderCapacity = 1000;
      StringBuilder builder = new StringBuilder(builderCapacity);

      // Get Sections
      List<Section> sections = propertiesDef.getSections();

      for(Section section : sections) {
         // For each section in Definitions, check if the key exists
         // If it doesn't exist, skip this value

      }

      return "";
   }
   /**
    * Loads the contents of a PropertiesDefinition into the PreferencesEnum.
    *
    * @param propertiesDef
    * @param preferences
    */
   public static void loadPropertiesDefinition(PropertiesDefinition propertiesDef, Preferences preferences) {
    
   }

}
