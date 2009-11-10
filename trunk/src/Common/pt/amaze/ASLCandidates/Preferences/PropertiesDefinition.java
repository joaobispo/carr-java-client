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

import java.util.ArrayList;
import pt.amaze.ASLCandidates.Interfaces.*;
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
public abstract class PropertiesDefinition {

   /**
    * Enables autosave by default. Initializes sections list with an initial
    * capacity of ten.
    */
   public PropertiesDefinition() {
      this(10);
   }

   /**
    * Enables autosave by default. Initializes sections list with the given
    * capacity.
    */
   public PropertiesDefinition(int capacity) {
      autosaveStatus = true;
      sections = new ArrayList<Section>(capacity);
      buildSections();
   }


   /**
    * @return the filename of the Properties file.
    */
   public abstract String getPropertiesFilename();

   /**
    * Builds the sections which will define the properties files. An
    * implementation of this method should be composed by consecutive calls to
    * addSection() methods.
    */
   protected abstract void buildSections();

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
   public abstract EnumKey valueOf(String keyName);

   /**
    * @return a list with Section objects, which define the properties file.
    */
   public List<Section> getSections() {
      return sections;
   }

   /**
    * When autosave is enabled, any modifications in the PreferencesEnum are 
    * immediately reflected in the Properties file.	
    * 
    * @return true if autosave is enabled. False otherwise.
    */
   public boolean isAutoSaveEnabled() {
      return autosaveStatus;
   }

   /**
    * Sets the status of autosave. By default, it is true.
    *
    * @param status
    */
   public void setAutoSave(boolean status) {
      autosaveStatus = status;
   }

   /**
    * Helper method for adding a new section to a list.
    *
    * @param newSections
    * @param comments
    * @param propertyName
    */
   protected void addSection(String comment, EnumKey propertyName) {
      Section section = new Section(comment, propertyName);
      sections.add(section);
   }

   /**
    * Helper method for adding a new section to a list.
    * 
    * @param newSections
    * @param comments
    * @param propertyName
    */
   protected void addSection(String comment) {
      Section section = new Section(comment);
      sections.add(section);
   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean autosaveStatus;
   private List<Section> sections;

   // Definitions for Section
   public static String PROPERTIES_COMMENT_PREFIX = "#";
   private static String LINE_SEPARATOR = System.getProperty("line.separator");
   private static String PROPERTY_SEPARATOR = " = ";


   /**
    * Class which stores a String[] and a EnumKey. The String[] represents
    * comments, which should appear before the EnumKey.
    */
   public class Section {

      public Section(String comment, EnumKey key) {
         this.comment = comment;
         this.key = key;
      }

      public Section(String comment) {
         this.comment = comment;
         this.key = null;
      }

      public String getComment() {
         return comment;
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
         int capacity = 200;
         StringBuilder builder = new StringBuilder(capacity);

         // Process comment
         // If empty, put an empty line
         if (comment.length() == 0) {
            builder.append(LINE_SEPARATOR);
         } else {
            // Otherwise, put the line with a comment prefix
            builder.append(PROPERTIES_COMMENT_PREFIX);
            builder.append(comment);
            builder.append(LINE_SEPARATOR);
         }


         // Check if property is not null and process it.
         if (key != null) {
            builder.append(key.getKey());
            builder.append(PROPERTY_SEPARATOR);
            builder.append(value);
            builder.append(LINE_SEPARATOR);
         }

         return builder.toString();
      }

      /**
       * @return the text of the properties file for this section, using
       * default values.
       */
      @Override
      public String toString() {
         if(key == null) {
            return toString(null);
         }
         else {
            return toString(key.getDefaultValue());
         }        
      }


      /**
       * INSTANCE VARIABLES
       */
      private final String comment;
      private final EnumKey key;
   }
}
