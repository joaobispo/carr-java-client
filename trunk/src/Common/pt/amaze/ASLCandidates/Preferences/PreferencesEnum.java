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

package pt.amaze.ASLCandidates.Preferences;

import java.io.File;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pt.amaze.ASLCandidates.Interfaces.EnumKey;
import pt.amaze.ASLCandidates.Interfaces.PropertiesDefinition;
import pt.amaze.ASLCandidates.IoUtils;
import pt.amaze.ASLCandidates.PreferencesUtil;

/**
 * Wrapper for Preferences class, which uses EnumKey instead of Strings to
 * access its values.
 * 
 * @author Joao Bispo
 */
public class PreferencesEnum {

   /**
    * Builds a PreferencesEnum. If local is true, fetches a UserNode for package
    * of class c. If local is false, fetches a SystemNode for package of class c.
    *
    * @param c Preferences of package of Class c will be fetched.
    * @param local
    */
   public PreferencesEnum(Class<?> c, boolean local) {
      this(c, local, null);
   }

   /**
    * Builds a PreferencesEnum. If local is true, fetches a UserNode for package
    * of class c. If local is false, fetches a SystemNode for package of class c.
    * If a PropertiesDefinition is given, it is used to back up the Preferences.
    *
    * @param c Preferences of package of Class c will be fetched.
    * @param local controls if class should fetch a SystemNode or a LocalNode.
    * @param propertiesDef Backs up the Preferences with a Properties file.
    */
   public PreferencesEnum(Class<?> c, boolean local, PropertiesDefinition propertiesDef) {
      if(local) {
         preferences = Preferences.userNodeForPackage(c);
      } else {
         preferences = Preferences.systemNodeForPackage(c);
      }

      this.propertiesDef = propertiesDef;

      if(propertiesDef != null) {
         initializeProperties(propertiesDef);
      }
   }

   /**
    * Loads the contents of a Properties object into the Preferences.
    * 
    * @param propertiesDef
    */
   private void initializeProperties(PropertiesDefinition propertiesDef) {
      // Properties filename
      String propertiesFilename = propertiesDef.getPropertiesFilename();
      // Load properties
      Properties properties = IoUtils.loadProperties(propertiesFilename);
      // Check if the Properties file exists
      if(properties == null) {
         // Create a new properties file.
         String propertiesContents = PreferencesUtil.generateProperties(propertiesDef, preferences);
         // Save file
         String propFilename = propertiesDef.getPropertiesFilename();
         File propFile = IoUtils.safeFile(propFilename);
         IoUtils.write(propFile, propertiesContents);
      } else {
         // Load properties values into preferences
         PreferencesUtil.loadPropertiesDefinition(propertiesDef, preferences);
      }

      /*
      if(properties != null) {

         newPreferences.loadProperties(properties);
      }
               Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Using properties file '"+propertiesDef.getPropertiesFilename()+"'.");
       */
   }

   /**
    * @param key key whose associated value is to be returned.
    * @return the value associated with the specified key in this preference
    * node. If there is no value associated with the specified key,
    * the default value defined in EnumKey is returned.
    */
   public String getPreference(EnumKey key) {
      return preferences.get(key.getKey(), key.getDefaultValue());
   }

   /**
    * @param key key whose associated value is to be returned.
    * @return the value associated with the specified key in this preference 
    * node. If there is no value associated with the specified key, 
    * null is returned.
    */
   public String getPreferenceReal(EnumKey key) {
      return preferences.get(key.getKey(), null);
   }


   /**
    * Associates the specified value with the specified key in this preference node.
    *
    * @param key key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    */
   public void putPreference(EnumKey key, String value) {
      preferences.put(key.getKey(), value);
   }

   /**
    * Loads the contents of the Properties object and saves the in the
    * Preferences.
    *
    * @param properties the properties object to import the values from.
    */
   public void loadProperties(Properties properties) {
      Set<String> keys = properties.stringPropertyNames();

      // Add properties to preferences
      for(String key : keys) {
         String value = properties.getProperty(key);

         //putPreference(, value);
         preferences.put(key, value);
      }

      /*
      if(keys.size() > 0) {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Added "+keys.size()+" entries to the Preferences.");

      } else {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Properties file was empty.");
      }
       */
   }

   
   /**
    * INSTANCE VARIABLES
    */
   private final Preferences preferences;
   private final PropertiesDefinition propertiesDef;



}