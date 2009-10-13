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
package pt.ualg.Car.JavaDriver.System;

import pt.ualg.Car.Controller.CarpadInput;

/**
 *
 * @author Joao Bispo
 */
public enum DriverInput {

   WHEEL(CarpadInput.WHEEL),
   TRIGGER(CarpadInput.TRIGGER);

   private DriverInput(CarpadInput controllerInput) {
      this.controllerInput = controllerInput;
   }

   public String getName() {
      return controllerInput.getName();
   }

   public int getControllerInputIndex() {
      return controllerInput.ordinal();
   }

   public static int numberOfInputs() {
      return values().length;
   }
   /**
    * INSTANCE VARIABLES
    */
   private final CarpadInput controllerInput;
}
