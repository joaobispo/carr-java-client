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

package pt.amaze.ASLCandidates.Interfaces;

import java.util.List;

/**
 * A class that implements this interface can define what contents a Properties
 * file should have, under the form of Section objects (comments and keys).
 *
 * <p>This interface is used by PreferencesEnum to implement support for
 * loading/saving properties files.
 * 
 * @author Joao Bispo
 */
public interface PropertiesDefinition {

   /**
    * @return a list with Section objects.
    */
   List<Section> getSections();

   /**
    * @return the filename of the Properties file.
    */
   String getPropertiesFilename();


   /**
    * Returns the enum constant of this type with the specified name.
    * The string must match exactly an identifier used to declare an enum
    * constant in this type.
    * (Extraneous whitespace characters are not permitted.)
    *
    * @param keyName
    * @return the enum constant with the specified name, or null if the enum
    * couldn't be found.
    */
   EnumKey valueOf(String keyName);

   /**
    * Class which stores a String[] and a EnumKey. The String[] represents
    * comments, which should appear before the EnumKey.
    */
   class Section {

      public Section(String[] comments, EnumKey key) {
         this.comments = comments;
         this.key = key;
      }

      public String[] getComments() {
         return comments;
      }

      public EnumKey getKey() {
         return key;
      }

      /**
       * Returns the text of the properties file for this section
       *
       * @param value
       * @return
       */
      public String toString(String value) {
         String commentPrefix = "#";
         String lineSeparator = System.getProperty("line.separator");
         String propertySeparator = " = ";

         int capacity = 200;
         StringBuilder builder = new StringBuilder(capacity);

         // Process comments
         for(String comment : comments) {
            // If empty, put an empty line
            if(comment.length() == 0) {
               builder.append(lineSeparator);
            } else {
            // Otherwise, put the line with a comment prefix
               builder.append(commentPrefix);
               builder.append(comment);
               builder.append(lineSeparator);
            }
         }

         // Process propertie
         builder.append(key.getKey());
         builder.append(propertySeparator);
         builder.append(value);
         builder.append(lineSeparator);

         return builder.toString();
      }

      /**
       * @return the text of the properties file for this section, using
       * default values.
       */
      @Override
      public String toString() {
         return toString(key.getDefaultValue());
      }



      /**
       * INSTANCE VARIABLES
       */
      private final String[] comments;
      private final EnumKey key;
   }
}
