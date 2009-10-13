/*
 *  Copyright 2009 Ancora Research Group.
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

package pt.ualg.Car.Controller;

/**
 * Has information about the inputs received from the controller for the car.
 * @author Joao Bispo
 */
public enum CarpadInput {
      SWITCH1("Analog 1"),
      SWITCH2("Analog 2"),
      SWITCH3("Analog 3"),
      SWITCH4("Analog 4"),
      WHEEL("Wheel"),
      TRIGGER("Trigger");

   private CarpadInput(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public static int numberOfInputs() {
      return values().length;
   }

      /**
       * INSTANCE VARIABLES
       */
      private final String name;

      // Signal sent by CarPad indicating start of a package.
      public static final int COMMAND_START = 255;
      public static final int NUMBER_OF_INPUTS = values().length;
}
