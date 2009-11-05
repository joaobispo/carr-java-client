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

package pt.ualg.AldricCar.CarClient;

import java.util.logging.Logger;
import pt.amaze.ASL.LoggingUtils;
import pt.amaze.ASLCandidates.RxtxUtils;

/**
 *
 * @author Joao Bispo
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //System.out.println("HELLO!");

    }

    /**
     * Method which tests the 1-byte counter, to see if it is possible to use it.
     */
    private static void counterTest() {


        byte byteone = Byte.MAX_VALUE;
        byte bytetwo = (byte) (byteone + 1);

        int intone = byteone;
        int inttwo = bytetwo;

        System.out.println("ByteTwo:"+bytetwo);
        System.out.println("ByteTwo - ByteOne:"+(byte)(bytetwo-byteone));
        System.out.println("IntTwo - IntOne:"+(inttwo-intone));
        System.out.println("IntTwo - IIntOnentOne (casting):"+(byte)(inttwo-intone));
    }

    /**
     * Standard initiallization for the program
     */
    public static void init() {
      // Logging
       LoggingUtils.setupConsoleOnly();
       pt.amaze.ASLCandidates.LoggingUtils.redirectSystemOut();
       pt.amaze.ASLCandidates.LoggingUtils.redirectSystemErr();

       // Check if libraries exists
       boolean librariesExist = RxtxUtils.rxtxLibrariesExists();

       if(!librariesExist) {
          Logger.getLogger(Main.class.getName()).
                  warning("Libraries not found. Exiting...");
          System.exit(1);
       }
   }
}
