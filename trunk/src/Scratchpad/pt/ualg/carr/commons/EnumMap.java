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

package pt.ualg.carr.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Store and Retrive Strings. Enums are used as keys.
 *
 * @author Joao Bispo
 */
public class EnumMap {



   public EnumMap(String databaseName) {
      this.database = new HashMap<String, String>();
      this.databaseName = databaseName;
   }

   /**
    * Associates the specified value with the specified key in this table.
    * If the table previously contained a mapping for the key, a warning is issued
    * to the console and the old value is replaced by the specified value.
    *
    * <p><b>*Prints Info Messages*</b>
    *
    * @param key a string.
    * @param value another string.
    */
   @SuppressWarnings("static-access")
   public void putString(String key, String value) {
      String returnString = database.put(key, value);

      if(returnString != null) {
         console.info("Value associated with key \""+key+"\" in datamap \""+
                 databaseName+"\" was replaced.");
         console.info("\t\""+returnString+"\" replaced by \""+value+"\".");
      }
   }

   public Set<String> keySet() {
      return database.keySet();
   }

   /**
    * Returns the string mapped to the given key
    *
    * <p><b>*Prints Info Messages*</b>
    *
    * @param key a string.
    * @return the string mapped by the key. If no mapping is found, returns an
    * empty string.
    */
   @SuppressWarnings("static-access")
   public String getString(String key) {
         String result = database.get(key);
         if(result == null) {
            console.info(notFoundMessage(key));
            console.info("\tReturning empty String.");
            return "";
         }
         else {
            return result;
         }
   }


   /**
    * Helper method for error messages.
    *
    * @param key a string.
    * @return Returns a String representing a message when a key is not found
    * in the database.
    */
   private String notFoundMessage(String key) {
      return "Key \""+key+"\" not found in database \""+databaseName+"\".";
   }

   public String getDatabaseName() {
      return databaseName;
   }

   @Override
   public String toString() {
      return database.toString();
   }


   /**
    * INSTANCE VARIABLES
    */

   private Map<String,String> database;
   private String databaseName;

   // For output of messages
   private static final Logger console = Logger.getLogger(EnumMap.class.getName());
}

