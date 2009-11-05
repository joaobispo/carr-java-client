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

import java.util.EnumMap;
import pt.ualg.AldricCar.CarClient.CommandModule.CommandSetup.Variable;

/**
 * Straightforward, unoptimized implementation of Command.
 * 
 * @author Joao Bispo
 */
public class CommandImplementation implements Command {

   public CommandImplementation(EnumMap<Variable, Integer> values) {
      this.values = values;
   }

   /**
    * INSTANCE VARIABLES
    */
   private final EnumMap<CommandSetup.Variable, Integer> values;

   public int getValue(Variable var) {
      return values.get(var);
   }
}
