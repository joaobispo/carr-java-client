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

package pt.ualg.AldricCar.CarClient.CarpadModule;

import gnu.io.SerialPort;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;
import pt.amaze.ASLCandidates.RxtxUtils;

/**
 * Utility methods for Carpad Module.
 * 
 * @author Joao Bispo
 */
public class CarpadUtils {

   /** 
    * @return the default serial port name of the Carpad, according to 
    * operating system. Currently supports Windows and Linux (Debian).
    */
   public static String defaultSerialPortName() {
      String os = System.getProperty("os.name");
      String lowerOs = os.toLowerCase();

      String defaultPort;

      if (lowerOs.startsWith("windows")) {
         defaultPort = "COM4";
         Logger.getLogger(CarpadUtils.class.getName()).
                 info("Using default value for serial port in Windows: '" + defaultPort + "'.");
      } else if (lowerOs.startsWith("linux")) {
         defaultPort = "/dev/ttyUSB0";
         Logger.getLogger(CarpadUtils.class.getName()).
                 info("Using default value for serial port in Linux: '" + defaultPort + "'.");
      } else {
         Logger.getLogger(CarpadUtils.class.getName()).
                 info("I don't know the default serial port " +
                 "for Operating System '" + os + "'. " +
                 "Using empty string as default port name.");
         defaultPort = "";
      }

      return defaultPort;
   }


   /**
    * Tries to find the name of the port to which the CarPad is connected.
    * Tests every serial port if finds, one by one, until a positive match appears.
    *
    * @return The name of the first port that tests positive is returned.
    * If none of the found ports test positive, null is returned.
    */
   public static String findCarpadPortName() {
      // Messages will be sent, get Logger
      Logger logger =  Logger.getLogger(CarpadUtils.class.getName());

      // Get list of Serial Port
      List<String> serialPorts = RxtxUtils.getSerialPortList();
      
      // Show which serial ports where found
      logger.info("Looking for Carpad. Found the following serial ports: "+serialPorts);

      // Test each port
      for(String portName : serialPorts) {
         logger.info("Testing port ["+portName+"]:");

         boolean isCarPadPort = testCarpadPort(portName);

         if(isCarPadPort) {
            logger.info("Found Carpad on port ["+portName+"]!");
            return portName;
         }
      }

      return null;
   }


   /**
    * Tests if the given port is connected to Carpad. It tries to connect to
    * the port. If it connects, it looks for the kind of input that is expected
    * from the Carpad.
    *
    * @param portName the name of the port to test.
    * @return true if it could connect to the port and could find the pattern of
    * Carpad. False otherwise.
    */
   static boolean testCarpadPort(String portName) {
      // Connect to the serial port
      SerialPort serialPort = RxtxUtils.openSerialPort(portName, "Testing port "+portName);

      if(serialPort == null) {
         return false;
      }

      // From this moment on, the serial port is connect. Before exiting the
      // method, it needs to be closed.

      try {
         // Listen to the port to see if the output is the expected
         InputStream inputStream = new BufferedInputStream(serialPort.getInputStream());

         boolean isCarPad = testInputStream(inputStream);

         // Stream was tested, port can be closed now.
         inputStream.close();
         serialPort.close();
         serialPort = null;

         return isCarPad;

      } catch (IOException ex) {
         // If there was an error, close the SerialPort and go to the next port.
         Logger.getLogger(CarpadUtils.class.getName()).
         warning("IOException while testing port ["+portName+"] for CarController.");
         serialPort.close();
         serialPort = null;
         return false;
      }
   }


   /**
    * Reads the input stream, looking for the output pattern of the Carpad.
    *
    * @param inputStream
    * @return
    */

   private static boolean testInputStream(InputStream inputStream) throws IOException {

      /*
      // Get the first data from the inputStream
      int readInt = inputStream.read();

      boolean isCommandStart = readInt == CarpadSetup.PREAMBLE;
      long initialNanos = System.nanoTime();

      // Read stream until a number signaling CommandStart appears
      while (!isCommandStart) {
         // Test if there is a timeout
         long elapsedTime = System.nanoTime() - initialNanos;
         long timeout = TimeUtils.millisToNanos(INPUTSTREAM_TIMEOUT_MILLIS);
         if (elapsedTime > timeout) {
            return false;
         }

         // Read again
         readInt = inputStream.read();
         isCommandStart = readInt == CarpadInput.COMMAND_START;

      }

      // Check for the following pattern:
      // 255 ([number] [number] ...){number of inputs} 255 ...

      // Search for a 255 inside an interval of numbers
      int period = CarpadInput.NUMBER_OF_INPUTS;
      int periodCounter = 0;
      while(readInt != 255) {
         readInt = inputStream.read();
         periodCounter++;

         // Check if it already passed the time a 255 should have appeard.
         if(periodCounter > period ) {
            return false;
         }
      }

      // Found a 255. Check if the next X numbers are not 255, and then appears
      // a 255 again
      for(int i=0; i<period; i++) {
         readInt = inputStream.read();
         if(readInt == 255) {
            return false;
         }
      }

      // Final check: if the next readInt is 255, we found a CarPad.
      readInt = inputStream.read();
      if(readInt == 255) {
         return true;
      } else {
         return false;
      }
       */

      return false;
   }
}
