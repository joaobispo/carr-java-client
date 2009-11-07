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

package pt.ualg.AldricCar.CarClient.ClientModule;

import java.util.ArrayList;
import java.util.List;
import pt.amaze.ASLCandidates.Interfaces.EnumKey;
import pt.amaze.ASLCandidates.Interfaces.PropertiesDefinition;

/**
 * Contains information about the Client’s Properties file.
 *
 * @author Joao Bispo
 */
public class ClientProperties implements PropertiesDefinition {

   public ClientProperties() {
      if(sections == null) {
         sections = buildSections();
      }
   }

   private List<Section> buildSections() {
      String[] comments;
      String propertieName;
      List<Section> newSections = new ArrayList<Section>(ClientPreferences.values().length);

      // Section 1
      comments = new String[]{
                 "#",
                 "Properties file for Aldric's Car Client",
                 "#",
                 "",
                 "",
                 "#",
                 "# Server",
                 "#",
                 "",
                 " Server Address"
              };
      propertieName = ClientPreferences.ServerAddress.getKey();
      addSection(newSections, comments, propertieName);

      return newSections;
   }


   private void addSection(List<Section> newSections, String[] comments, String propertieName) {
      Section section = new Section(comments, null)
      throw new UnsupportedOperationException("Not yet implemented");
   }

   public boolean containsName(String keyName) {
      try {
         ClientPreferences.valueOf(keyName);
      } catch (IllegalArgumentException ex) {
         return false;
      }
      return true;
   }


   public List<Section> getSections() {
      return sections;
   }

   public String getPropertiesFilename() {
      return CLIENT_PROPERTIES_FILENAME;
   }

   /**
    * INSTANCE VARIABLES
    */
   private static List<Section> sections = null;
   private static final String CLIENT_PROPERTIES_FILENAME = "client.properties";


}
