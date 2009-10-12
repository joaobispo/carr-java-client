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

package pt.ualg.Car;

import pt.ualg.Car.common.PrefEnum;
import java.util.prefs.Preferences;

/**
 *
 * @author Joao Bispo
 */
public class SimplePrefs {

   public SimplePrefs(PrefEnum options, boolean userNode) {
      this.options = options;
      if (userNode) {
         prefs = Preferences.userNodeForPackage(options.getClassValue());
      } else {
         prefs = Preferences.systemNodeForPackage(options.getClassValue());
      }
   }

   

   /**
    * INSTANCE VARIABLES
    */
   private PrefEnum options;
   private Preferences prefs;
}
