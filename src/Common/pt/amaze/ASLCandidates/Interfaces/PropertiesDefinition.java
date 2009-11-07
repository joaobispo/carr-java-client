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
    * Checks if the given name is part of the EnumKey this class is supporting.
    *
    * @param keyName
    * @return true if String corresponds to a valid key name of the EnumKey
    * that is being backed-up.
    */
   boolean containsName(String keyName);

   /**
    * Returns the line in the Properties file where the given property appears.
    *
    * @param keyName
    * @return
    */
   int propertyLine(String keyName);

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
       * INSTANCE VARIABLES
       */
      private final String[] comments;
      private final EnumKey key;
   }
}
