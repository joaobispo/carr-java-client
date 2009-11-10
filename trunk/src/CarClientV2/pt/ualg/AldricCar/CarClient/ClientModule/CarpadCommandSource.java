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

package pt.ualg.AldricCar.CarClient.ClientModule;

import java.util.EnumMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import pt.amaze.ASLCandidates.Concurrent.ReadChannel;
import pt.amaze.ASLCandidates.Identification.ByteIdentifier;
import pt.amaze.ASLCandidates.ParseUtils;
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;
import pt.ualg.AldricCar.CarClient.CarpadModule.CarpadReader;
import pt.ualg.AldricCar.CarClient.CarpadModule.CarpadSetup;
import pt.ualg.AldricCar.CarClient.CarpadModule.CarpadUtils;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.Command;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.CommandImplementation;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.CommandSource;

/**
 *
 * @author Joao Bispo
 */
public class CarpadCommandSource implements CommandSource {

   public CarpadCommandSource(ByteIdentifier byteIdentifier, long readTimeout) {
      this.byteIdentifier = byteIdentifier;
      this.readTimeout = readTimeout;
      carpadReader = new CarpadReader();
      channel = carpadReader.getReadChannel();
      carpadExecutor = Executors.newSingleThreadExecutor();
      preferences = ClientPreferences.getPreferences();
      isCarpadRunning = false;
      firstRead = true;

      // Read First Read Timeout from Preferences
      String longString = preferences.getPreference(ClientPreferences.FirstReadTimeoutMillis);
      firstReadTimeoutMillis = ParseUtils.parseLong(longString);
   }



   public boolean connect() {
      // Check if CarpadReader is already running
      if(isConnected()) {
         Logger.getLogger(CarpadCommandSource.class.getName()).
                 warning("CarpadReader is already running.");
         return true;
      }

      // Get PortName
      String portName = getPortName();

      // Check if a portName could be found
      if(portName == null) {
         return false;
      }

      // Found Carpad, try to connected it.
      // At this point:
      //   - CarpadExecutor should be ready to accept new tasks;
      //   - CarpadReader should be in NOT_ACTIVE state;
      boolean isActivated = carpadReader.activate(portName);
      if(!isActivated) {
         Logger.getLogger(CarpadCommandSource.class.getName()).
                 warning("Could not activate CarpadReader.");
         return false;
      }

      // Carpad is active
      carpadExecutor.submit(carpadReader);
      carpadExecutor.shutdown();

      // Activate the flags.
      firstRead = true;
      isCarpadRunning = true;

      return true;
   }

   public void disconnect() {
      // Check if CarpadReader task was submitted
      if(!isCarpadRunning) {
         Logger.getLogger(CarpadCommandSource.class.getName()).
                 warning("CarpadReader is already disconnected.");
         return;
      }

      // Check if Carpad already terminated
      if (!carpadExecutor.isTerminated()) {
         // Terminate it
         carpadExecutor.shutdownNow();
         try {
            boolean terminatedCarpad = carpadExecutor.awaitTermination(CARPAD_READER_TIMEOUT_MILLIS, TimeUnit.DAYS);

            if (!terminatedCarpad) {
               // Couldn't stop carpadReader!!!
               Logger.getLogger(CarpadCommandSource.class.getName()).
                       severe("Couldn't stop CarpadReader!!! Instatiating a new CarpadReader...");

               carpadReader = new CarpadReader();
               channel = carpadReader.getReadChannel();

            }
         } catch (InterruptedException ex) {
            // Interrupt thread
            Thread.currentThread().interrupt();
         }
      }


      carpadExecutor = Executors.newSingleThreadExecutor();
      isCarpadRunning = false;
      firstRead = true;
   }

   public boolean isConnected() {
      // If flag is false, Carpad is not running
      if(!isCarpadRunning) {
         return false;
      }

      // Even if flag is true, CarpadReader might have stopped
      if(carpadExecutor.isTerminated()) {
         disconnect();
         return false;
      }

      // If CarpadReader is still running, signal as true
      return true;
   }

   public long getCommandPeriod() {
      return CARPAD_PERIOD_MILLIS;
   }

   public Command readCommand() {
      // If not running, return null
      if(!isConnected()) {
         Logger.getLogger(CarpadCommandSource.class.getName()).
                 warning("Is not connected.");
         return null;
      }

      // Try to read a command. If it's the first time reading, the Timeout is
      // longer.
      long timeout;
      if(firstRead) {
         timeout = firstReadTimeoutMillis;
         firstRead = false;
      } else {
         timeout = readTimeout;
      }

      int[] values = null;
      try {
         values = channel.poll(timeout, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
         // Thread interrupted. Just stay interrupted
         Thread.currentThread().interrupt();
      }

      if(values == null) {
         System.out.println("Timeout while reading from channel. Current timeout: '"+timeout+"' millis.");
         return null;
      }

      return buildCommand(values);
   }

   public void setTimeout(long timeout) {
      readTimeout = timeout;
   }

   public long getTimeout() {
      return readTimeout;
   }

   public void setByteIdentifier(ByteIdentifier byteIdentifier) {
      this.byteIdentifier = byteIdentifier;
   }

   public ByteIdentifier getByteIdentifier() {
      return byteIdentifier;
   }


   /**
    * The name where Carpad is connected. First, it tests the last known port
    * to which Carpad was connected. If Carpad is not found there, it looks
    * for a port where Carpad might be connected. If none is found, null is
    * returned.
    *
    * If a port to where Carpad is connected is found, that portname is saved
    * to the Preferences of the Client.
    *
    * @return the name of the serial port to which Carpad is connected. If
    * a port where carpad is connected could not be found, null is returned.
    */
   private String getPortName() {
            String lastKnownPortName = 
                    preferences.getPreference(ClientPreferences.SerialPortName);

            // Test port
            boolean foundCarpad = CarpadUtils.testCarpadPort(lastKnownPortName);
            if(foundCarpad) {
               return lastKnownPortName;
            }

            // Carpad not found! Try to find it.
            String newPortName = CarpadUtils.findCarpadPortName();

            // If a port is found, save it and return it.
            if(newPortName != null) {
               preferences.putPreference(ClientPreferences.SerialPortName, newPortName);
               return newPortName;
            }

            // Could not find a port to where Carpad is connected
            return null;
   }

   /**
    * @return a Command object built from the Carpad data
    */
   private Command buildCommand(int[] carpadValues) {
      EnumMap<Command.Variable, Integer> commandValues = 
              new EnumMap<Command.Variable, Integer>(Command.Variable.class);

      // Get counter
      final int counter = byteIdentifier.newByte();

      // Get carpad values
      int trigger = carpadValues[CarpadSetup.Input.TRIGGER.getIndex()];
      int wheel = carpadValues[CarpadSetup.Input.WHEEL.getIndex()];
      int pan = carpadValues[CarpadSetup.Input.ANALOG1.getIndex()];
      int tilt = carpadValues[CarpadSetup.Input.ANALOG2.getIndex()];
      int flags1 = carpadValues[CarpadSetup.Input.ANALOG3.getIndex()];
      int flags2 = carpadValues[CarpadSetup.Input.ANALOG4.getIndex()];


      commandValues.put(Command.Variable.COUNTER, counter);
      commandValues.put(Command.Variable.TRIGGER, trigger);
      commandValues.put(Command.Variable.WHEEL, wheel);
      commandValues.put(Command.Variable.PAN, pan);
      commandValues.put(Command.Variable.TILT, tilt);
      commandValues.put(Command.Variable.FLAGS_FIRST_SET, flags1);
      commandValues.put(Command.Variable.FLAGS_SECOND_SET, flags2);
      

      return new CommandImplementation(commandValues);
   }

   /**
    * INSTANCE VARIABLES
    */
   private CarpadReader carpadReader;
   private ExecutorService carpadExecutor;
   private ReadChannel<int[]> channel;
   private PreferencesEnum preferences;

   /**
    * The flag should never be true if CarpadReader hasn't been started.
    * The flag might be true even if CarpadRead has already stoped.
    */
   private boolean isCarpadRunning;

   // Miscelaneous state variables
   private ByteIdentifier byteIdentifier;
   private long readTimeout;
   private boolean firstRead;

   // Definitions
   private final long firstReadTimeoutMillis;
   private final long CARPAD_READER_TIMEOUT_MILLIS = 5000;
   // The period with which the carpad sends values. This value is coded inside
   // the microcontroller program.
   private final long CARPAD_PERIOD_MILLIS = 40;

}
