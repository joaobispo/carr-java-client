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
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;
import pt.amaze.ASLCandidates.Concurrent.ReadChannel;
import pt.amaze.ASLCandidates.Concurrent.WriteChannel;
import pt.amaze.ASLCandidates.RxtxUtils;

/**
 * Accesses the data of Carpad hardware through the serial port.
 *
 * <p>The methods of this object shouldn’t be accessed while it is running
 * in a separate thread, since they are not thread safe.
 * It is the responsibility of the object using CarpadReader to know if it is
 * currently running or not.
 * 
 * @author Joao Bispo
 */
public class CarpadReader implements Runnable {

   /**
    * Builds a CarpadReader, in NOT_ACTIVE state.
    */
   public CarpadReader() {
      isActive = false;
      serialPort = null;
      inputStream = null;
      writeChannel = new WriteChannel<int[]>(1);
   }

   /**
    * @return a ReadChannel from where we can read the values from the Carpad.
    */
   public ReadChannel<int[]>  getReadChannel() {
      return writeChannel.getReadChannel();
   }

   /**
    * If in NOT_ACTIVE state, attempts a connection to the given <tt>portName</tt>.
    * If a connection is possible, object is put in ACTIVE state.
    * <p>Before connecting, the method tests if the given <tt>portName</tt>
    * gives a correct stream of inputs.
    *
    * @param portName name of the port where Carpad is connected.
    * @return Returns true if it could connect to the port. If it couldn't connect,
    * or if tried to connect while in ACTIVE state, returns false.
    */
   public boolean activate(String portName) {
      // Check state
      if(isActive) {
         logger.warning("CarpadRead is active, it can't be activated.");
         return false;
      }

      // Test port
      boolean isPortGood = CarpadUtils.testCarpadPort(portName);

      if(!isPortGood) {
         return false;
      }

      // Make connection
      serialPort = RxtxUtils.openSerialPort(portName, "CarPad Controller at '"+portName+"'.");

      if(serialPort == null) {
         return false;
      }

      // From this moment on, the serial port is connect. If a problem happens,
      // it needs to be closed.
      try {
         // Connect the InputStream
         inputStream = serialPort.getInputStream();
      } catch (IOException ex) {
         logger.warning("Could open serial port, but couldn't connect to Inputstream.");
         freeResources();
         return false;
      }

      // Object is ready and active
      isActive = true;

      return true;
   }

   /**
    * If in ACTIVE state, liberates resources associated with the object and puts the object in NOT_ACTIVE state. As any of the methods of the object, it can only be called if object is currently not running in another thread.
    */
   public void deactivate() {
      // Check state
      if (!isActive) {
         logger.warning("CarpadRead is inactive, it can't be deactivated.");
      }

      // Free Resources
      freeResources();

      // Change state
      isActive = false;
   }

   /**
    * The main loop of the object. On each cycle, reads the values from the
    * serial port, builds an array with the read values and puts it in its
    * WriteChannel.
    *
    * <p>If the Channel is already full, the array is discarded.
    * If connection is lost, or values could not be read, the loop terminates
    * but resources are not liberated.
    */
   public void run() {
      // Check if state is ACTIVE
      if(!isActive) {
         logger.warning("Trying to start CarpadReader without being in ACTIVE state.");
      }

      // Variable for dropping statistics
      int droppedValues = 0;
      int totalValues = 0;

      // Run while the thread is not interrupted
      boolean isRunning = true;
      while(isRunning) {
         // Read an array of values from the Carpad
         final int[] values = readValues(inputStream, logger);
         System.out.println("Read Values:"+Arrays.toString(values));

         // Check if values could be read
         if(values == null) {
            System.out.println("Null values.");
            isRunning = false;
            break;
         }

         // Put the values on the channel
         boolean operationSuccessful = writeChannel.offer(values);

         // Dropping Statistics
         if(!operationSuccessful) {
            droppedValues++;
         }
         totalValues++;

         // Check if dropped packets achieved threshold
         if(totalValues >= SAMPLE_SIZE) {
            if(droppedValues >= WARNING_RATE_THRESHOLD) {
               float rate = (float)droppedValues / (float)totalValues;
               int roundedRate = (int) (rate * 100);
               logger.info("Carpad values dropping rate at "+roundedRate+"%");
            }

            // Reset values
            totalValues = 0;
            droppedValues = 0;
         }

         // Check if thread was interrupted
         if(Thread.currentThread().isInterrupted()) {
            isRunning = false;
         }
      }

      if(Thread.interrupted()) {
         deactivate();
         Thread.currentThread().interrupt();
      } else {
         deactivate();
      }
   }

   /**
    * Frees any resources that might be needed for an activation.
    */
   private void freeResources() {
      // Close Inputstream
      if(inputStream != null) {
         try {
            inputStream.close();
         } catch (IOException ex) {
            logger.warning("IOException while trying to close InputStream of CarController.");
         }
         inputStream = null;
      }

      // Close Serial Port
      if(serialPort != null) {
         serialPort.close();
         serialPort = null;
      }
      
      // Clean WriteChannel
      writeChannel.clear();
   }


   /**
    * Reads a message from the Carpad.
    *
    * @return an array of ints with the values of the controller.
    */
   private static int[] readValues(InputStream inputStream, Logger logger) {
      System.out.println("Inside readValues");
      // Number of inputs
      final int numInputs = CarpadSetup.INPUTS.length;

      // Create return array
      final int[] values = new int[numInputs];

      // Check if inputStream is in phase
      boolean isInPhase = putInputstreamInPhase(inputStream, logger);
      if(!isInPhase) {
         logger.warning("Could not get InputStream in phase.");
         return null;
      }

      // Stream is now in phase. Read the values into the array.
      for(int i=0; i<numInputs; i++) {
         try {
            values[i] = inputStream.read();
         } catch (IOException ex) {
            logger.warning("IOException while reading Carpad values");
            return null;
         }
      }

      // Return values
      return values;
   }

   /**
    * If the first read input is not the preamble, the inputstream might be
    * tries to get the inputStream in phase.
    * 
    * <p>The InputStream is in phase when the preamble was the last value
    * read from the InputStream.
    *
    * @return true if inputStream could be put in phase. False otherwise.
    */
   private static boolean putInputstreamInPhase(InputStream inputStream, Logger logger) {
      // Number of inputs
      final int numInputs = CarpadSetup.INPUTS.length;

      try {
         int value = inputStream.read();
         if (value != CarpadSetup.PREAMBLE) {
            System.out.println("Is not in phase.");
            System.out.println("Out of phase:"+value);
            // It is not in phase. Read up to a maximum of the number of inputs,
            // until preable value appears.
            int counter = 0;
            while (value != CarpadSetup.PREAMBLE) {
               // Check if counter has reached maximum value
               if (counter > numInputs) {
                  logger.warning("Could not get InputStream in phase.");
                  return false;
               }

               value = inputStream.read();
               System.out.println("Out of phase:"+value);
            }
         }
      } catch (IOException ex) {
         logger.warning("IOException while tring to get inputstream in phase.");
         return false;
      }

      return true;
   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean isActive;
   private SerialPort serialPort;
   private InputStream inputStream;
   private WriteChannel<int[]> writeChannel;

   // Message Dropping Checker
   private final int SAMPLE_SIZE = 100;
   private final int WARNING_RATE_THRESHOLD = SAMPLE_SIZE / 10; // 10% of SAMPLE_SIZE

   // Utils
   private final Logger logger = Logger.getLogger(CarpadReader.class.getName());



}
