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

import java.util.prefs.Preferences;
import pt.amaze.ASLCandidates.Interfaces.EnumKey;

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
    * INSTANCE VARIABLES
    */
   private final Preferences preferences;
}
