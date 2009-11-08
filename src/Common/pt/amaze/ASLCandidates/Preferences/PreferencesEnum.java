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
      if(local) {
         preferences = Preferences.userNodeForPackage(c);
      } else {
         preferences = Preferences.systemNodeForPackage(c);
      }

      this.propertiesDef = null;
      //initializeProperties(propertiesDef);
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
   /*
   public PreferencesEnum(Class<?> c, boolean local, PropertiesDefinition propertiesDef) {
      if(local) {
         preferences = Preferences.userNodeForPackage(c);
      } else {
         preferences = Preferences.systemNodeForPackage(c);
      }

      this.propertiesDef = propertiesDef;
      initializeProperties(propertiesDef);
   }
    */

   /**
    * If Properties file exists, loads the contents into the Preferences. Else,
    * a new Properties file is created.
    * 
    * @param propertiesDef
    */
   /*
   private void initializeProperties(PropertiesDefinition propertiesDef) {
      if(propertiesDef == null) {
         return;
      }

      // Properties filename
      String propertiesFilename = propertiesDef.getPropertiesFilename();

      // Load properties
      Properties properties = IoUtils.loadProperties(propertiesFilename);

      // Check if the Properties file exists
      if (properties == null) {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Properties file doesn't exist. Creating a new one...");
         PreferencesUtil.savePropertiesDefinition(this);
      } else {
         // Load properties values into preferences
         PreferencesUtil.loadPropertiesDefinition(this);
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Loaded values from file '" + propertiesDef.getPropertiesFilename() + "'.");
      }
   }
    */

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
    *<p> - Changes in Preferences are reflected on the fields of the properties 
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
      if(propertiesDef != null) {
         String oldFile = propertiesDef.getPropertiesFilename();
         String newFile = propertiesDefinition.getPropertiesFilename();
         Logger.getLogger(PreferencesEnum.class.getName()).
                 warning("PreferencesEnum already associated with file '"+oldFile+"'. " +
                 "Associating with new file '"+newFile+"'");
      }

      propertiesDef = propertiesDefinition;

      // Properties filename
      String propertiesFilename = propertiesDefinition.getPropertiesFilename();
      File propertiesFile = new File(propertiesFilename);
      // Load properties
      //Properties properties = IoUtils.loadProperties(propertiesFilename);

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
    * Everytime a value if put using this method, the properties file is saved
    * (if there is one associated).
    *
    * @param key key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    */
   public void putPreference(EnumKey key, String value) {
      putPreference(key, value, true);
   }

   /**
    * Associates the specified value with the specified key in this preference node.
    *
    * @param key key with which the specified value is to be associated.
    * @param value value to be associated with the specified key.
    * @param saveProperties if true, all changes of preferences will be saved
    * in the properties file associated.
    */
   public void putPreference(EnumKey key, String value, boolean saveProperties) {
      preferences.put(key.getKey(), value);

      if (saveProperties) {
         if (propertiesDef != null) {
            /*
            Logger.getLogger(PreferencesEnum.class.getName()).
                    info("Saving properties file.");
             */
            PreferencesUtil.savePropertiesDefinition(this);
         }
      }
   }

   /**
    * Loads the contents of the Properties object and saves the in the
    * Preferences.
    *
    * @param properties the properties object to import the values from.
    */
   /*
   public void loadProperties(Properties properties) {
      Set<String> keys = properties.stringPropertyNames();

      // Add properties to preferences
      for(String key : keys) {
         String value = properties.getProperty(key);

         //putPreference(, value);
         preferences.put(key, value);
      }
*/
      /*
      if(keys.size() > 0) {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Added "+keys.size()+" entries to the Preferences.");

      } else {
         Logger.getLogger(PreferencesEnum.class.getName()).
                 info("Properties file was empty.");
      }
       */
   /*
   }
    */

   
   /**
    * INSTANCE VARIABLES
    */
   private final Preferences preferences;
   private PropertiesDefinition propertiesDef;



}
