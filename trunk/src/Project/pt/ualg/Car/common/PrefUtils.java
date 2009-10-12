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

package pt.ualg.Car.common;

import java.util.prefs.Preferences;

/**
 * Utility methods for easier use of Preferences API with enum objets.
 *
 * @author Joao Bispo
 */
public class PrefUtils {

   public static String getPref(Preferences prefs, PrefEnum option) {
      return prefs.get(option.getName(), option.defaultValue());
   }

   public static int getPrefInt(Preferences prefs, PrefEnum option) {
      String intValue = prefs.get(option.getName(), option.defaultValue());
      return ParsingUtils.parseInt(intValue);
   }

   public static void putPref(Preferences prefs, PrefEnum option, Object value) {
      prefs.put(option.getName(), value.toString());
   }
}
