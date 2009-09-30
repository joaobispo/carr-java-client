/*
 *  Copyright 2009 Ancora Research Group.
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

package pt.ualg.carr.gui2;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ualg.carr.client1.Command;

/**
 * Represents the serial Port to communicate with the Car Pad.
 *
 * @author Joao Bispo
 */
public class ControllerSerialPort {


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

      // Test each port
      for(String portName : serialPorts) {
         System.out.print("Testing port ["+portName+"]:");
         boolean isCarPadPort = testPort(portName);
         if(isCarPadPort) {
            System.out.println(" - Found CarPad!");
            return portName;
         }
      }

      return null;

      
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

      try {
         // Obtain a CommPortIdentifier object for the port you want to open
         CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);

         // Get the port's ownership
         serialPort = (SerialPort) portId.open(portMessage, 5000);

      } catch (NoSuchPortException ex) {
         logger.warning("Serial Port '" + portName + "' not found.");
         return null;
      } catch (PortInUseException ex) {
         logger.warning("Serial Port '" + portName + "' is already in use.");
         return null;
      }

      return serialPort;
   }

   /**
    * Tests if a given serial port is connected to a Car Pad.
    *
    * @param portName
    * @return
    */
   private static boolean testPort(String portName) {
      
      // Connect to the serial port
      SerialPort serialPort = connectSerial(portName, "Testing port "+portName);

      if(serialPort == null) {
         return false;
      }

      // From this moment on, the serial port is connect. Before exiting the
      // method, it needs to be closed.

      try {
         // Listen to the port to see if the output is the expected
         InputStream inputStream = serialPort.getInputStream();

         boolean isCarPad = testInputStream(inputStream);
         // After testInputStream, we should add a new line to System.out.
         //System.out.println("");

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
    * Reads the input stream as looks for the pattern of the Car Pad input.
    * 
    * @param inputStream
    * @return
    */
   private static boolean testInputStream(InputStream inputStream) throws IOException {

      // Check if port is outputing '-1'
      int readInt = inputStream.read();

      // If it is a -1, accept some '-1' before discarding it
      boolean isMinusOne = readInt == -1;
      if(isMinusOne) {
         int counter = 0;
         System.out.print("|");
         while(isMinusOne) {
            readInt = inputStream.read();
            isMinusOne = readInt == -1;
            //System.out.println("ReadInt:"+readInt + " ("+counter+")");

            counter++;
            // Update counter status
            if(counter == COUNTER_TIMEOUT_FIRST_QUARTER) {
               System.out.print("25|");
            }
            if(counter == COUNTER_TIMEOUT_SECOND_QUARTER) {
               System.out.print("50|");
            }
            if(counter == COUNTER_TIMEOUT_THIRD_QUARTER) {
               System.out.print("75|");
            }

            // If counter timeout, conclude this is not the portif()
            boolean counterTimeout = counter > COUNTER_TIMEOUT;
            if(counterTimeout) {
               System.out.print("100|");
               System.out.println(" - Timeout");
               return false;
            }
         }
      }
      // Add a new line.
      //System.out.println("");

      // It is outputing stuff besides '-1'. Check for the following pattern:
      // 255 [number] [number] ... 255

      // Search for a 255 inside an interval of numbers
      int period = Command.NUM_PORTS;
      int periodCounter = 0;
      while(readInt != 255) {
         readInt = inputStream.read();
         periodCounter++;

         // Check if it already passed the time a 255 should have appeard.
         if(periodCounter >= period ) {
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
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(ControllerSerialPort.class.getName());
   private static final int COUNTER_TIMEOUT = 100;
   private static final int COUNTER_TIMEOUT_FIRST_QUARTER = (COUNTER_TIMEOUT / 4);
   private static final int COUNTER_TIMEOUT_SECOND_QUARTER = (COUNTER_TIMEOUT / 4) * 2;
   private static final int COUNTER_TIMEOUT_THIRD_QUARTER = (COUNTER_TIMEOUT / 4) * 3;



}
