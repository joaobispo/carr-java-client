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

package pt.ualg.Car.JavaDriver.Controller;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import pt.amaze.ASL.TimeUtils;
import pt.ualg.Car.Controller.CarpadInput;

/**
 * Utility methods for CarpadPort.
 *
 * @author Joao Bispo
 */
public class CarpadPortUtils {
   /**
    * @return the default COM port name, according to operating system.
    */
   public static String defaultCommPortName() {
      String os = System.getProperty("os.name");
      String lowerOs = os.toLowerCase();

      String defaultPort;

      if (lowerOs.startsWith("windows")) {
         defaultPort = "COM4";
         logger.info("Using default value for serial port in Windows: '" + defaultPort + "'.");
      } else if (lowerOs.startsWith("linux")) {
         defaultPort = "/dev/ttyUSB0";
         logger.info("Using default value for serial port in Linux: '" + defaultPort + "'.");
      } else {
         logger.info("I don't the default serial port " +
                 "for Operating System '" + os + "'. " +
                 "Using empty string as default port name.");
         defaultPort = "";
      }

      return defaultPort;
   }

   /**
    * Tests if a given serial port is connected to a Car Pad.
    *
    * @param portName
    * @return
    */
   public static boolean testPort(String portName) {
      // Connect to the serial port
      SerialPort serialPort = connectSerial(portName, "Testing port "+portName);

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
         logger.warning("IOException while finding CarController.");
         serialPort.close();
         serialPort = null;
         return false;
      }
   }

   /**
    * Tries to connectSerial to the serial port identified by portName. If a connection
    * is not possible, null is returned.
    *
    * @param portName
    * @return
    */
   public static SerialPort connectSerial(String portName, String portMessage) {
      SerialPort serialPort = null;
      int millisWait = 500;
      int inputStreamReadTimeoutMillis = 2000;

      try {
         // Obtain a CommPortIdentifier object for the port you want to open
         CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);

         // Get the port's ownership
         serialPort = (SerialPort) portId.open(portMessage, millisWait);

         // Setup the Serial Port
         serialPort.setSerialPortParams(9600,
                 SerialPort.DATABITS_8,
                 SerialPort.STOPBITS_1,
                 SerialPort.PARITY_NONE);


         serialPort.enableReceiveTimeout(inputStreamReadTimeoutMillis);

      } catch (NoSuchPortException ex) {
         logger.info("Serial Port '" + portName + "' not found.");
         return null;
      } catch (PortInUseException ex) {
         logger.info("Serial Port '" + portName + "' is already in use.");
         return null;
      } catch (UnsupportedCommOperationException e) {
         logger.warning("Serial Port '" + portName + "' could not be setup.");
         return null;
      }

      return serialPort;
   }

   /**
    * Reads the input stream as looks for the pattern of the Car Pad input.
    *
    * <p>This method is not safe, because it can block on reading an inputstream
    * which will never have something to read. The method 'testInputStreamSafe'
    * should be used instead.
    *
    * @param inputStream
    * @return
    */
   public static boolean testInputStream(InputStream inputStream) throws IOException {

      // Get the first data from the inputStream
      int readInt = inputStream.read();

      boolean isCommandStart = readInt == CarpadInput.COMMAND_START;
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

   }

      /**
    * Tries to find the name of the port to which the CarPad is connected. The
    * method connects to all the Serial Ports it finds and tests if the output
    * is the expected.
    *
    * <p> If no port is found, null is returned.
    *
    * @return
    */
   public static String findCarController() {
      // Get list of Serial Port
      List<String> serialPorts = listSerialPorts();
      logger.info("Found serial ports: "+serialPorts);
      //System.out.println("Found serial ports: "+serialPorts);

      // Test each port
      for(String portName : serialPorts) {
         System.out.print("Testing port ["+portName+"]:");

         boolean isCarPadPort = testPort(portName);

         if(isCarPadPort) {
            System.out.println(" - Found CarPad!");
            return portName;
         } else {
            System.out.println(" - Not Found.");
         }
      }

      return null;

   }

   /**
    * List the available serial ports
    *
    * @return Array of string for the available serial port names
    */
   public static List<String> listSerialPorts() {
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      List<String> portList = new ArrayList<String>();

      while (ports.hasMoreElements()) {
         CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
         if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            portList.add(port.getName());
         }
      }

      return portList;
   }

   /**
    * VARIABLES
    */
   // Constants
   private static final long INPUTSTREAM_TIMEOUT_MILLIS = 1000;
   // Utils
   private static final Logger logger = Logger.getLogger(CarpadPortUtils.class.getName());
}
