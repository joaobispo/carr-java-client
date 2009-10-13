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

package pt.ualg.Car.JavaDriver;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.amaze.ASL.LoggingUtils;
import pt.ualg.Car.Controller.CarpadMessage;
import pt.ualg.Car.JavaDriver.Controller.CarpadPort;
import pt.ualg.Car.common.LoggingUtils2;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       init();

        carpadPortTest();
    }

   private static void carpadPortTest() {
      CarpadPort carpadPort = new CarpadPort();

      boolean connected = carpadPort.connect();

      System.out.println("connected? "+connected);
      boolean run = true;

      /*
      while (run) {
         try {
            CarpadMessage message = carpadPort.readMessage();
            System.out.println(message.toString());
         } catch (IOException ex) {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
            carpadPort.disconnect();
            run = false;
         }
      }
       */

      //carpadPort.disconnect();
   }

   private static void init() {
      // Setup Logger
       LoggingUtils.setupConsoleOnly();
       LoggingUtils2.redirectSystemOut();
       LoggingUtils2.redirectSystemErr();

       // See if the needed libraries are present
       testLibraryExists();
   }

   private static void testLibraryExists() {
        String os = System.getProperty("os.name");
        String lowerOs = os.toLowerCase();
        System.out.println("Running on '"+os+"'.");

        if(lowerOs.startsWith("windows")) {
            // Check for DLLs
            File rxtxSerial = new File("rxtxSerial.dll");
            if(!rxtxSerial.exists()) {
                System.out.println("Missing file: rxtxSerial.dll. Exiting...");
                System.exit(1);
            }
        } else {
            System.out.println("Operating System '"+os+"' not supported.");
            //System.exit(1);
        }
    }

}
