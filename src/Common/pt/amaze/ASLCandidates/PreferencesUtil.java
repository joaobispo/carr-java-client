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

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pt.amaze.ASLCandidates.Interfaces.EnumKey;
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
   public static String generateProperties(PreferencesEnum preferences) {
      // Get Properties Definition
      PropertiesDefinition propertiesDef = preferences.getPropertiesDefinition();
      
      int builderCapacity = 1000;
      StringBuilder builder = new StringBuilder(builderCapacity);

      // Get Sections
      List<Section> sections = propertiesDef.getSections();

      for(Section section : sections) {
         /*
         // For each section in Definitions, check if the key exists
         EnumKey enumKey = section.getKey();
         enumKey = propertiesDef.valueOf(enumKey.getKey());

         // If it doesn't exist, skip this value
         if(enumKey == null) {
            Logger.getLogger(PreferencesUtil.class.getName()).
                    warning("");
            continue;
         }
          */
         // For each section, get the value in Preferences and add the text
         // to the properties file
         String value = preferences.getPreference(section.getKey());
         String sectionContent = section.toString(value);
         builder.append(sectionContent);

      }

      return builder.toString();
   }

   /**
    * Loads the contents of a PropertiesDefinition into the PreferencesEnum.
    *
    * @param propertiesDef
    * @param preferences
    * @return true if it could load the values of Properties file.
    */
   public static boolean loadPropertiesDefinition(PreferencesEnum preferences) {
      // Get the Properties object
      PropertiesDefinition propertiesDefiniton = preferences.getPropertiesDefinition();
      String propertiesFilename = propertiesDefiniton.getPropertiesFilename();
      Properties properties = IoUtils.loadProperties(propertiesFilename);

      if(properties == null) {
         return false;
      }

      // Get keys
      Set<String> propertyKeys = properties.stringPropertyNames();
      for(String key : propertyKeys) {
         // Check if key exists in PropertiesDefinition
         EnumKey enumKey = propertiesDefiniton.valueOf(key);
         if(enumKey == null) {
            Logger.getLogger(PreferencesUtil.class.getName()).
                    warning("Key '"+key+"' in properties '"+propertiesFilename+"' " +
                    "doesn't exist in the program definitions.");
         } else {
            // Get the value
            String value = properties.getProperty(key);
            // Store it in the preferences
            preferences.putPreference(enumKey, value, false);
         }
      }

      return true;
   }


   /**
    * Saves the content of the preferences to a properties file.
    *
    * @param propertiesDef
    * @param preferences
    */
   public static void savePropertiesDefinition(PreferencesEnum preferences) {

      // Create a new properties file.
      String propertiesContents = PreferencesUtil.generateProperties(preferences);

      // Save file
      PropertiesDefinition propertiesDef = preferences.getPropertiesDefinition();
      String propFilename = propertiesDef.getPropertiesFilename();
      File propFile = IoUtils.safeFile(propFilename);
      if (propFile != null) {
         IoUtils.write(propFile, propertiesContents);
      }
 
   }

}
