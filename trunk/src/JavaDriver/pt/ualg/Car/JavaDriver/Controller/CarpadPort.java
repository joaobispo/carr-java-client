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

import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pt.ualg.Car.Controller.CarpadInput;
import pt.ualg.Car.Controller.CarpadMessage;
import pt.ualg.Car.JavaDriver.PrefCarpad;
import pt.ualg.Car.common.PrefUtils;

/**
 * Has the resposability to discover and connect to the serial port
 * and to communicate with the Carpad.
 *
 * @author Joao Bispo
 */
public class CarpadPort {

   public CarpadPort() {
      isConnected = false;
      serialPort = null;
      inputStream = null;
   }

   /**
    * Attempts to connect to the Carpad through the serial port.
    * 
    * @return true if it could connect, false otherwise.
    */
   public boolean connect() {
      // Load carpadPortName
      String carpadPortName = loadCarpadPortName();
      
      // Test if CarPad is connect to the loaded port
      boolean isConnectedToPort = CarpadPortUtils.testPort(carpadPortName);

      // If not, try to find the correct port
      if(!isConnectedToPort) {
         carpadPortName = CarpadPortUtils.findCarController();
      }

      // Test again. If null, carpadPort could not be found
      if(carpadPortName == null) {
         return false;
      }

      // Connection is possible, try to establish communication.
      serialPort = CarpadPortUtils.connectSerial(carpadPortName, "CarPad Controller at '"+carpadPortName+"'.");

      if(serialPort == null) {
         logger.warning("Could not connect to serial port '"+carpadPortName+"'.");
         return false;
      }

      // From this moment on, the serial port is connect. If a problem happens,
      // it needs to be closed.
      try {
         // Connect the InputStream
         inputStream = serialPort.getInputStream();
      } catch (IOException ex) {
         logger.warning("Could open serial port, but couldn't connect to Inputstream.");
         disconnect();
         return false;
      }

      isConnected = true;
      return true;
   }

   /**
    * Cleans all resources associated with CarpadPort. This includes:
    *
    * <p>Inputstream;
    * <br>SerialPort;
    *
    */
   public void disconnect() {
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

      // Update status
      isConnected = false;
            
   }

   /**
    * Reads a message from the Carpad
    *
    * @return a CarpadMessage, if it could read a message.
    * @throws IOException If there is any error while reading from the carpad.
    * Usually this means it has been compromised and object should be shutdown.
    */
   public CarpadMessage readMessage() throws IOException {
      if(!isConnected) {
         logger.warning("Trying to read message from Carpad without being connected.");
         throw new IOException();
      }

      if(inputStream == null) {
         logger.warning("Trying to read message from Carpad, but inputstream is closed.");
         throw new IOException();
      }

      // Read first
      int counter = 0;

      while(counter < READ_MESSAGE_MAX_DROPS) {
      int readInt = inputStream.read();
            // If read number is commandStart, process package and put it in the queue.
            if (readInt == CarpadInput.COMMAND_START) {
               return processCommand(inputStream);
            } else {
               counter++;
            }
      }

      logger.warning("Could not read message from Carpad.");
      
      return null;
   }

   /**
    * Creates and returns a Command object.
    */
   private CarpadMessage processCommand(InputStream inputStream) throws IOException {
      // Create array for inputs
      int[] angles = new int[CarpadInput.NUMBER_OF_INPUTS];

      // Read as many commands as necessary
      for(int i=0; i<CarpadInput.NUMBER_OF_INPUTS; i++) {
            angles[i] = inputStream.read();
      }

      // Build the command object
      CarpadMessage message = new CarpadMessage(angles);

      return message;
   }



   /**
    * @return the name of the last stored name of the CommPort, or the default
    * CommPort name if there is none stored.
    */
   private String loadCarpadPortName() {
      Preferences preferences = PrefCarpad.getPreferences();
      String carpadPortName = preferences.get(PrefCarpad.CommPortNameString.name(), null);

      if(carpadPortName == null) {
         carpadPortName = CarpadPortUtils.defaultCommPortName();
      }

      return carpadPortName;
   }

   private void storeCarpadPortName(String carpadPortName) {
      prefs.putPref(PrefCarpad.CommPortNameString, carpadPortName);
   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean isConnected;

   private SerialPort serialPort;
   InputStream inputStream;

   // Utils
   private static final Logger logger = Logger.getLogger(CarpadPort.class.getName());
   private PrefUtils prefs = PrefCarpad.getPrefUtils();

   // Constants
   private static final int READ_MESSAGE_MAX_DROPS = CarpadInput.NUMBER_OF_INPUTS;
}
