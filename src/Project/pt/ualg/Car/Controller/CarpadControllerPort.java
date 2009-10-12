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

package pt.ualg.Car.Controller;

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
import pt.ualg.Car.common.Concurrent.ReadChannel;
import pt.ualg.Car.common.Concurrent.WriteChannel;

/**
 * Represents the serial Port to communicate with the Car Pad.
 *
 * @author Joao Bispo
 */
public class CarpadControllerPort implements Runnable {

   // Creates an object using the default serial port.
   // This port is operating-system dependent.
   public CarpadControllerPort() {
      // Assign default ports, according to operating system
      this(defaultCommPortName());
   }

   public CarpadControllerPort(String commPortName) {
      // Sanitize commPortName
      if (commPortName == null) {
         this.commPortName = "";
      } else {
         this.commPortName = commPortName;
      }

      final int channelCapacity = 1;
      this.channel = new WriteChannel<ControllerMessage>(channelCapacity);
      run = false;
      state = CarpadState.INITIALIZING;
   }


   public ReadChannel getReadChannel() {
      return channel.getReadChannel();
   }


   /**
    * Blocks until Carpad Controller has initialized.
    * <p> Warning: This method can only be called after Carpad is running,
    * or else it will block forever.
    */
   public void waitInitialization() {
      // Removed this piece of code because we could issue a carpad object
      // to a thread, and this method be called first.
      /*
      if(state == null) {
         logger.warning("Carpad is not running yet!");
         return;
      }
       */

      long SLEEP_WAIT = 100;


      while (state == CarpadState.INITIALIZING) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 1.");
            Thread.currentThread().interrupt();
         }
      }

   }

   @Override  
   public void run() {
      state = CarpadState.INITIALIZING;
      
      // Test if CarPad is connect to port
      boolean isConnected = testPort(commPortName);

      // If not, try to find the correct port
      if(!isConnected) {
         commPortName = findCarController();
      }

      // Test again for null, commPortName could have been not found
      if(commPortName == null) {
         //logger.warning("Could not found CarPad.");
         state = CarpadState.TERMINATED;
         return;
      }

      // Connection is possible, try to establish communication.
      run = true;
      SerialPort serialPort = connectSerial(commPortName, "CarPad port at '"+commPortName+"'.");

      if(serialPort == null) {
         logger.warning("Could not connect to serial port '"+commPortName+"'.");
         run = false;
         state = CarpadState.TERMINATED;
         return;
      }
      // From this moment on, the serial port is connect. Before exiting the
      // method, it needs to be closed.

      // Declare the inputStream
      InputStream inputStream = null;

      try {

         // Create InputStream
         inputStream = serialPort.getInputStream();
         state = CarpadState.RUNNING;
         // Read input stream as fast as it can
         while (run) {
            int readInt = inputStream.read();

            // If read number is commandStart, process package and put it in the queue.
            if (readInt == COMMAND_START) {
               processCommand(inputStream);
            }
            // If it is not, go to the next cycles, until it appears

         }

      } catch (IOException ex) {
         // If there was an error, prepare to terminate thread.
         logger.warning("IOException while using CarController.");
         run = false;
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
      
      state = CarpadState.TERMINATED;
   }

    /**
    * Creates a Command object and puts it in the queue, if the queue is empty.
    */
   private void processCommand(InputStream inputStream) throws IOException {
      // Create array for inputs
      int[] angles = new int[NUMBER_OF_INPUTS];

      // Read as many commands as necessary
      for(int i=0; i<NUMBER_OF_INPUTS; i++) {
            angles[i] = inputStream.read();
      }

      // Build the command object
      ControllerMessage message = new ControllerMessage(angles);

      //Command command = new Command(packetCounter, angles);
      //packetCounter++;

      // Try to put object in queue
      boolean success = channel.offer(message);


      if(!success) {
         logger.info("Dropped message "+message.getCounter());
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
    * Reads the input stream as looks for the pattern of the Car Pad input.
    *
    * <p>This method is not safe, because it can block on reading an inputstream
    * which will never have something to read. The method 'testInputStreamSafe'
    * should be used instead.
    * 
    * @param inputStream
    * @return
    */
   private static boolean testInputStream(InputStream inputStream) throws IOException {      

      // Get the first data from the inputStream
      int readInt = inputStream.read();
      
      boolean isCommandStart = readInt == COMMAND_START;
      long initialNanos = System.nanoTime();

      // Read stream until a number signaling CommandStart appears
      while (!isCommandStart) {
         // Test if there is a timeout
         long elapsedTime = System.nanoTime() - initialNanos;
         if (elapsedTime > INPUTSTREAM_TIMEOUT_NANOS) {
            return false;
         }

         // Read again
         readInt = inputStream.read();
         isCommandStart = readInt == COMMAND_START;

      }
      
      // Check for the following pattern:
      // 255 ([number] [number] ...){number of inputs} 255 ...

      // Search for a 255 inside an interval of numbers
      int period = NUMBER_OF_INPUTS;
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

   public CarpadState getState() {
      return state;
   }

   /**
    * @return the name of the port it is currently using.
    */
   public String getCommPortName() {
      return commPortName;
   }



   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(CarpadControllerPort.class.getName());

   private static final long INPUTSTREAM_TIMEOUT_MILLIS = 1000;
   private static final long INPUTSTREAM_TIMEOUT_NANOS = TimeUtils.millisToNanos(INPUTSTREAM_TIMEOUT_MILLIS);
   
   // Signal sent by CarPad indicating start of a package.
   private static final int COMMAND_START = ControllerInput.COMMAND_START;
   // Number of inputs of the controller
   private static final int NUMBER_OF_INPUTS = ControllerInput.numberOfInputs();

   // Name of the Communication Port
   private String commPortName;
   // Indicates if the object should run or not.
   private boolean run;
   // Channel to where the commands will be sent.
   private WriteChannel<ControllerMessage> channel;

   private CarpadState state;


}
