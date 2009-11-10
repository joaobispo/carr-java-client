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

package pt.amaze.ASLCandidates;

import java.util.logging.Logger;

/**
 * Utility methods for Parsing
 *
 * @author Joao Bispo
 */
public class ParseUtils {

   /**
    * Tries to parse a String into a integer. If an exception happens, warns the
    * user and returns a 0.
    *
    * @param integer
    * @return the intenger represented by the string, or 0 if it couldn't be parsed.
    */
   public static int parseInt(String integer) {
      int intResult = 0;
      try {
         intResult = Integer.parseInt(integer);
      } catch (NumberFormatException e) {
         Logger.getLogger(ParseUtils.class.getName()).
                 warning("Couldn't parse '"+integer+"' into an integer. Returning "+intResult+".");
      }

      return intResult;
   }

   /**
    * Tries to parse a String into a long. If an exception happens, warns the
    * user and returns a 0.
    *
    * @param longNumber
    * @return the intenger represented by the string, or 0 if it couldn't be parsed.
    */
   public static long parseLong(String longNumber) {
      long longResult = 0;
      try {
         longResult = Long.parseLong(longNumber);
      } catch (NumberFormatException e) {
         Logger.getLogger(ParseUtils.class.getName()).
                 warning("Couldn't parse '"+longNumber+"' into an long. Returning "+longResult+".");
      }

      return longResult;
   }

}
