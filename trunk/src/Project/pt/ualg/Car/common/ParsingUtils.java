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

import java.util.logging.Logger;

/**
 * Utility methods for expedit parsing of values.
 *
 * @author Joao Bispo
 */
public class ParsingUtils {

   /**
    * Given a String, tries to parse it into a integer. If an exception occours,
    * 0 is returned.
    *
    * @param integer
    * @return
    */

   public static int parseInt(String integer) {
      int intResult = 0;
      try {
         intResult = Integer.parseInt(integer);
      } catch (NumberFormatException e) {
         logger.warning("Could not parse '"+integer+"' to Integer . Returning "+intResult+".");
      }

      return intResult;
   }

   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(ParsingUtils.class.getName());
}
