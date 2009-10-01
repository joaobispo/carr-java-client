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
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import pt.amaze.ASL.TimeUtils;
import pt.ualg.carr.client1.Command;

/**
 * Represents the serial Port to communicate with the Car Pad.
 *
 * @author Joao Bispo
 */
public class ControllerSerialPort implements Runnable {

   // Creates an object using the default serial port.
   // This port is operating-system dependent.
   public ControllerSerialPort(BlockingQueue<Command> channel) {
      // Assign default ports, according to operating system
      this(defaultCommPortName(), channel);
   }

   public ControllerSerialPort(String commPortName, BlockingQueue<Command> channel) {
      this.commPortName = commPortName;
      this.channel = channel;
      run = false;
      packetCounter = 0;
   }





   @Override  
   public void run() {
      // Test if port name is not null. If it is, give mock content
      if (commPortName == null) {
         commPortName = "";
      }

      // Test if CarPad is connect to port
      boolean isConnected = testPort(commPortName);
      // If not, try to find the correct port
      if(!isConnected) {
         commPortName = findCarController();
      }

      // Test again for null, commPortName could have been not found
      if(commPortName == null) {
         logger.warning("Could not found CarPad.");
         return;
      }

      // Connection is possible, try to establish communication.
      run = true;
      SerialPort serialPort = connectSerial(commPortName, "CarPad port at '"+commPortName+"'.");

      if(serialPort == null) {
         run = false;
         return;
      }
      // From this moment on, the serial port is connect. Before exiting the
      // method, it needs to be closed.

      // Declare the inputStream
      InputStream inputStream;

      try {

      // Create InputStream
      inputStream = serialPort.getInputStream();

      // Read input stream as fast as it can
      while(run) {
         int readInt = inputStream.read();

         // If read number is commandStart, process package and put it in the queue.
         if(readInt == COMMAND_START) {
            processCommand(inputStream);
         }
         // If it is not, go to the next cycles, until it appears

      }

      } catch (IOException ex) {
         // If there was an error, close the SerialPort and go to the next port.
         logger.warning("IOException while using CarController.");
         serialPort.close();
         serialPort = null;
         run = false;
         return;
      }

      // Finished execution. Close open streams
      if(inputStream != null) {
         try {
            inputStream.close();
         } catch (IOException ex) {
            logger.warning("IOException while trying to close InputStream of CarController.");
         }
      }

      // Close Serial Port
      serialPort.close();
      serialPort = null;
   }

    /**
    * Creates a Command object and puts it in the queue, if the queue is empty.
    */
   private void processCommand(InputStream inputStream) throws IOException {
      // Create array for inputs
      int[] angles = new int[Command.NUM_PORTS];

      // Read as many commands as necessary
      for(int i=0; i<Command.NUM_PORTS; i++) {
            angles[i] = inputStream.read();
      }

      // Build the command object
      Command command = new Command(packetCounter, angles);
      packetCounter++;

      // Try to put object in queue
      boolean success = channel.offer(command);


      if(!success) {
         logger.info("Dropped command "+command.getCounter());
      }

   }

   /**
    * Stops the reading of CarPad input signals
    */
   public void shutdown() {
      run = false;
   }

   /**
    * @return the default COM port name, according to operating system.
    */
   private static String defaultCommPortName() {
      String os = System.getProperty("os.name");
      String lowerOs = os.toLowerCase();

        if(lowerOs.startsWith("windows")) {
           return "COM4";
        } else if(lowerOs.startsWith("linux")){
            return "/dev/ttyUSB0";
        } else {
         logger.warning("Operating System '"+os+"' not supported, returning empty string.");
         return "";
        }
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
      int millisWait = 500;

      try {
         // Obtain a CommPortIdentifier object for the port you want to open
         CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);

         // Get the port's ownership
         serialPort = (SerialPort) portId.open(portMessage, millisWait);

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
      long initialNanos = System.nanoTime();
      if(isMinusOne) {
         //int counter = 0;
         //System.out.print("|");
         while(isMinusOne) {
            // Test if there is a timeout
            long elapsedTime = System.nanoTime() - initialNanos;
            if(elapsedTime > INPUTSTREAM_TIMEOUT_NANOS) {
               System.out.println("Timeout");
               return false;
            }
            readInt = inputStream.read();
            isMinusOne = readInt == -1;
            //System.out.println("ReadInt:"+readInt + " ("+counter+")");

            /*
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
             */

            // If counter timeout, conclude this is not the portif()
           /*
            boolean counterTimeout = counter > COUNTER_TIMEOUT;
            if(counterTimeout) {
               System.out.print("100|");
               System.out.println(" - Timeout");
               return false;
            }
            */
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
   /*
   private static final int COUNTER_TIMEOUT = 40;
   private static final int COUNTER_TIMEOUT_FIRST_QUARTER = (COUNTER_TIMEOUT / 4);
   private static final int COUNTER_TIMEOUT_SECOND_QUARTER = (COUNTER_TIMEOUT / 4) * 2;
   private static final int COUNTER_TIMEOUT_THIRD_QUARTER = (COUNTER_TIMEOUT / 4) * 3;
    */
   private static final long INPUTSTREAM_TIMEOUT_NANOS = TimeUtils.millisToNanos(3000);
   // Signal sent by CarPad indicating start of a package.
   private static final int COMMAND_START = 255;
   // Name of the Communication Port
   private String commPortName;
   // Indicates if the object should run or not.
   private boolean run;
   // Number to be send in the generated command packet
   private int packetCounter;
   // Queue to where the commands will be sent.
   private BlockingQueue<Command> channel;






}
