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

/**
 * Contains the information to send to the car.
 *
 * @author Joao Bispo
 */
public interface Command {

   /**
    * @return the contents of the Command over a byte array format. 
    * Useful for sending the information over UDP.
    */
   byte[] getByteArray();

   /**
    * 
    * @param var the CommandSetup.Variable whose associated value is to be returned
    * @return the value to which the specified CommandSetup.Variable is mapped,
    * or null if this map contains no mapping for the CommandSetup.Variable.
    */
   int getValue(Command.Variable variable);

   /**
    * List of the variables of the Command object
    */
   enum Variable {

      COUNTER,
      WHEEL,
      TRIGGER,
      PAN,
      TILT,
      FLAGS_FIRST_SET,
      FLAGS_SECOND_SET;
   }
   /**
    * The number of variables of Command
    */
   public int NUM_INPUTS = Variable.values().length;

   /**
    *  An array with the order by which the Variables are encoded in the byte array.
    */
   public static Variable[] VARIABLE_ORDER = {
      Variable.COUNTER,
      Variable.WHEEL,
      Variable.TRIGGER,
      Variable.PAN,
      Variable.TILT,
      Variable.FLAGS_FIRST_SET,
      Variable.FLAGS_SECOND_SET
   };
}
