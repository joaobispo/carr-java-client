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

package pt.amaze.ASLCandidates.Time;

import java.util.logging.Logger;

/**
 * Experimental Global Timer for taking times between an execution line which
 * may spawn more than one thread.
 *
 * @author Joao Bispo
 */
public class GlobalTimer {

   public static void startTimer() {
      if(isCounting) {
           Logger.getLogger(GlobalTimer.class.getName()).
                 warning("Timer has already started. Stop it first.");
         return;
      }
      
      isCounting = true;
      startTimeNanos = System.nanoTime();
      
   }

   public static void stopTimer() {
      long stopTime = System.nanoTime();
      if(!isCounting) {
         Logger.getLogger(GlobalTimer.class.getName()).
                 warning("Timer was stopped without starting first.");
         return;
      }

      lastRecordedIntervalNanos = stopTime - startTimeNanos;
      isCounting = false;
   }

   public static long getIimeIntervalNanos() {
      if(isCounting) {
          Logger.getLogger(GlobalTimer.class.getName()).
                 warning("Timer is still running. Stop it first.");
         return 0;
      }
      
      return lastRecordedIntervalNanos;
   }

   /**
    * INSTANCE VARIABLE
    */
   volatile private static long lastRecordedIntervalNanos = 0;
   volatile private static long startTimeNanos = 0;
   volatile private static boolean isCounting = false;
}
