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

package pt.ualg.AldricCar.CarClient.CommunicationsModule;

import java.util.EnumMap;

/**
 * Straightforward, simple implementation of Command.
 * 
 * @author Joao Bispo
 */
public class CommandImplementation implements Command {

   public CommandImplementation(EnumMap<Command.Variable, Integer> values) {
      this.values = values;
   }


   public int getValue(Command.Variable variable) {
      return values.get(variable);
   }

   @Override
   public String toString() {
      return values.toString();
   }


   public byte[] getByteArray() {
      Variable[] variableOrder = Command.VARIABLE_ORDER;
      int arraySize = variableOrder.length;
      byte[] byteArray = new byte[arraySize];

      // Fill array
      for(int i=0; i<arraySize; i++) {
         byteArray[i] = values.get(variableOrder[i]).byteValue();
      }

      return byteArray;
   }

   /**
    * INSTANCE VARIABLES
    */
   private final EnumMap<Command.Variable, Integer> values;


}
