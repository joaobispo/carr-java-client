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
import pt.ualg.Car.common.ParsingUtils;

/**
 *
 * @author Joao Bispo
 */
public enum Option implements PrefEnum {

   CalibrationWheelNeutralInt("93"),
   CalibrationTriggerNeutralInt("86"),
   CalibrationWheelSensitivityInt("3"),
   CalibrationTriggerSensitivityInt("4"),
   KeyMapWheelUp("76"), //KeyEvent.VK_L;
   KeyMapWheelDown("74"), //KeyEvent.VK_J;
   KeyMapTriggerUp("65"), //KeyEvent.VK_A;
   KeyMapTriggerDown("90"), //KeyEvent.VK_Z;
   CommPortNameString(null);

   private Option(String defaultValue) {
      this.defaultValue = defaultValue;
   }

   @Override
   public String defaultValue() {
      return defaultValue;
   }

   @Override
   public Class<?> getClassValue() {
      return classValue;
   }

   @Override
   public String getName() {
      return name();
   }



   //
   // INSTANCE VARIABLES
   //
   private final String defaultValue;
   
   /**
    * This class value, for easier summoning of Preferences.
    */
   public static final Class<?> classValue = pt.ualg.Car.Option.class;
}
