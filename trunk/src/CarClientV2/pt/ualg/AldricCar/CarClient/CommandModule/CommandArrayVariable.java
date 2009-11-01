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

package pt.ualg.AldricCar.CarClient.CommandModule;

/**
 * Enum representing the variables inside the command.
 * 
 * @author Joao Bispo
 */
public enum CommandArrayVariable {

   COUNTER(0),
   ANALOG1(1),
   ANALOG2(2),
   ANALOG3(3),
   ANALOG4(4),
   WHEEL(5),
   TRIGGER(6);

   private CommandArrayVariable(int index) {
      this.index = index;
   }

   /**
    * @return the index associated with the variable.
    */
   public int getIndex() {
      return index;
   }

   /**
    * @return the number of variables of a Command array
    */
   static int getArraySize() {
      return arraySize;
   }


   /**
    * INSTANCE VARIABLE
    */
   private final int index;
   private static final int arraySize = CommandArrayVariable.values().length;
}
