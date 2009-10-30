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

import java.util.logging.Logger;

/**
 * Contains the information to send to the car
 *
 * @author Joao Bispo
 */
public class Command {

   public Command(int[] values) {
      int expectedSize = CommandVariable.values().length;

      if(values.length != expectedSize) {
         this.values = new int[expectedSize];
         Logger.getLogger(Command.class.getName()).warning(
                 "Array size ("+values.length+") different of expected size " +
                 "("+expectedSize+"). Building Command with empty array");
      } else {
         this.values = values;
      }
   }

   /**
    * @return the internal array of the command
    */
   public int[] getValues() {
      return values;
   }

   /**
    *
    * @param var
    * @return the respective value.
    */
   public int getValue(CommandVariable var) {
      return values[var.getIndex()];
   }



   /**
    * INSTANCE VARIABLES
    */
   private final int[] values;
}
