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

package pt.ualg.AldricCar.CarClient.CarpadModule;

/**
 * Contains information about the Carpad, such as the value of the preamble
 * and which variables it sends.
 *
 * @author Joao Bispo
 */
public interface CarpadSetup {
   /**
    * The value of the preamble.
    */
    int PREAMBLE = 255;

   /**
    * Enum listing the inputs of the Carpad
    */
   enum Input {
      ANALOG1,
      ANALOG2,
      ANALOG3,
      ANALOG4,
      WHEEL,
      TRIGGER;

   };

   /**
    * The number of inputs of Carpad.
    */
   static int NUM_INPUTS = Input.values().length;

}
