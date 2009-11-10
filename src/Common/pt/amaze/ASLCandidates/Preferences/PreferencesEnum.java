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
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pt.amaze.ASLCandidates.Interfaces.EnumKey;
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
      if(local) {
         preferences = Preferences.userNodeForPackage(c);
      } else {
         preferences = Preferences.systemNodeForPackage(c);
      }

      this.propertiesDef = null;
      hasProperties = false;
      //initializeProperties(propertiesDef);
   }


   /**
    * Adds a PropertiesDefinition to the Preferences, so preferences are 
    * backed up by a properties file, along the mechanism for Preferences. 
    * After assigning a PropertiesDefinition, they influence Perferences in the 
    * following way:
    * 
    *<p> - Right after being added, values from the properties file are loaded into
    * the preferences; if properties file doesn’t exist, it is created with the 
    * current values of preferences.
    * 
    *<p> - If autosave is enabled in the PropertiesDefinition, changes in
    * Preferences are immediately reflected on the fields of the properties
    * file.
    * 
    * @param properties
    */
   public void addProperties(PropertiesDefinition propertiesDefinition) {
      if(propertiesDefinition == null) {
          Logger.getLogger(PreferencesEnum.class.getName()).
                 warning("null PropertiesDefinition.");
          return;
      }

      // Check if there wasn't already a PropertiesDefinition associated with
      // PreferencesEnum
      if(hasProperties) {
         String oldFile = propertiesDef.getPropertiesFilename();
         String newFile = propertiesDefinition.getPropertiesFilename();
         Logger.getLogger(PreferencesEnum.class.getName()).
                 warning("PreferencesEnum already associated with file '"+oldFile+"'. " +
                 "Associating with new file '"+newFile+"'");
      }

      propertiesDef = propertiesDefinition;
      hasProperties = true;

      // Properties filename
      String propertiesFilename = propertiesDefinition.getPropertiesFilename();
      File propertiesFile = new File(propertiesFilename);

      // Check if the Properties file exists
      if (!propertiesFile.isFile()) {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Properties file '"+propertiesFilename+"' doesn't exist. Creating a new one...");
          PreferencesUtil.savePropertiesDefinition(this);
      } else {
         // Load properties values into preferences
         boolean couldLoad = PreferencesUtil.loadPropertiesDefinition(this);
         String prefix;
         if(!couldLoad) {
            prefix = "Couldn't load";
         } else {
            prefix = "Loaded";
         }
         
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info(prefix + " values from file '" + propertiesDef.getPropertiesFilename() + "'.");
      }
   }

   /**
    * @return the PropertiesDefinition associated to this PreferencesEnum. 
    * If none is associated, null is returned.
    */
   public PropertiesDefinition getPropertiesDefinition() {
      return propertiesDef;
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
    * <p>If there is a PropertiesDefinition associated with this PreferenceEnum,
    * and the PropertiesDefinition has AutoSave enabled, everytime a value if
    * put using this method, the properties file is saved
    *
    * @param key key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    */
   public void putPreference(EnumKey key, String value) {
      preferences.put(key.getKey(), value);

      if(hasProperties) {
         if(propertiesDef.isAutoSaveEnabled()) {
            PreferencesUtil.savePropertiesDefinition(this);
         }
      }
   }


   /**
    * If a PropertiesDefinition is associated with PreferencesEnum, updates the
    * properties file with the current values.
    *
    * @return true if the file could be successfully written.
    */
   public boolean saveProperties() {
      if (!hasProperties) {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("There is no PropertiesDefinition class associated! Can't save properties.");
         return false;
      }

      return PreferencesUtil.savePropertiesDefinition(this);
   }

   
   /**
    * INSTANCE VARIABLES
    */
   private final Preferences preferences;
   private PropertiesDefinition propertiesDef;
   private boolean hasProperties;

}
