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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.CarpadMessage;
import pt.ualg.Car.common.Concurrent.ReadChannel;
import pt.ualg.Car.common.Concurrent.WriteChannel;

/**
 * Runs in another thread to fill a channel with messages from Carpad.
 *
 * @author Joao Bispo
 */
public class CarpadExec  {

   public CarpadExec() {
      t_state = CarpadState.IDLE;
      t_channel = new WriteChannel<CarpadMessage>(1);
      t_carpad = new CarpadPort();

      carpadExec = Executors.newSingleThreadExecutor();
   }

   /**
    * Tries to activate this module.
    * 
    * @return true if it could activate
    */
   public boolean activate() {
      if(t_state != CarpadState.IDLE) {
         logger.warning("Could not start CarpadExec because state is '" +
                       t_state + "' instead of " + CarpadState.IDLE + ".");
         return false;
      }

      Runnable runnable = getRunnable();
  

      return false;
   }

/**
 * 
 * @return the program that will run on the ExecutorService
 */
   private Runnable getRunnable() {
      return new Runnable() {

         @Override
         public void run() {
            if (t_state != CarpadState.IDLE) {
               logger.warning("Could not start CarpadExec because state is '" +
                       t_state + "' instead of " + CarpadState.IDLE + ".");
               return;
            }

            // Change state to INITIALLIZING
            t_state = CarpadState.INITIALIZING;

            // Connect to Carpad
            t_carpad.connect();

            // If couldn't connect, return
            if (!t_carpad.isConnected()) {
               internalShutdown();
               return;
            }

            // Start filling the queue
            t_state = CarpadState.RUNNING;
            while (t_carpad.isConnected()) {
               CarpadMessage message = t_carpad.readMessage();

               if (message != null) {
                  // Try to put object in queue
                  boolean success = t_channel.offer(message);

                  if (!success) {
                     logger.info("Dropped message " + message.getCounter());
                  }
               }
            }

            // Is no longer connected. Shutdown
            internalShutdown();
         }
      };
   }

   /**
    * @return ReadChannel where messages from Carpad are written.
    */
   public ReadChannel getReadChannel() {
      return t_channel.getReadChannel();
   }


   private void internalShutdown() {
      // Clear channel
      t_channel.clear();

      // Disconnect from Carpad
      if(t_carpad.isConnected()) {
         t_carpad.disconnect();
      }

      t_state = CarpadState.IDLE;
   }

   /**
    * Blocks until Carpad Controller has initialized.
    * <p> Warning: This method can only be called after Carpad is running,
    * or else it will block forever.
    */
   private void waitInitialization() {
      long SLEEP_WAIT = 100;


      while (t_state == CarpadState.INITIALIZING) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 1.");
            Thread.currentThread().interrupt();
         }
      }

   }

   /**
    * INSTANCE VARIABLES
    */
   //
   private ExecutorService carpadExec;

   // State of the object
   private CarpadState t_state;
   // Channel to where the commands will be sent.
   private WriteChannel<CarpadMessage> t_channel;
   // Carpad controller
   private CarpadPort t_carpad;


   // Utils
   private Logger logger = Logger.getLogger(CarpadExec.class.getName());

  enum CarpadState {
      INITIALIZING,
      RUNNING,
      IDLE;
   }
}
