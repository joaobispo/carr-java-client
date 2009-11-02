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

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.amaze.ASLCandidates.Logging.LoggingOutputStream;

/**
 * Utility methods for Java Logger.
 *
 * @author Joao Bispo
 */
public class LoggingUtils {

   /**
    * Redirect System.out to the info level of the logger.
    */
   public static void redirectSystemOut() {
      // Get Root Logger
      Logger logger = Logger.getLogger("");

      // Build Printstream for System.out
      LoggingOutputStream los = new LoggingOutputStream(logger, Level.INFO);
      PrintStream outPrint = new PrintStream(los, true);

      // Set System.out
      System.setOut(outPrint);
   }

   /**
    * Redirect System.err to the warning level of the logger.
    */
   public static void redirectSystemErr() {
      // Get Root Logger
      Logger logger = Logger.getLogger("");

      // Build Printstream for System.out
      LoggingOutputStream los = new LoggingOutputStream(logger, Level.WARNING);
      PrintStream outPrint = new PrintStream(los, true);

      // Set System.out
      System.setErr(outPrint);
   }

   // preserve old stdout/stderr streams in case they might be useful
   public final static PrintStream stdout = System.out;
   public final static PrintStream stderr = System.err;
}
