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

package pt.ualg.Car.System;

import pt.ualg.Car.Controller.CarpadMessageListener;
import pt.ualg.Car.Controller.CarpadMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import pt.amaze.ASL.TimeUtils;
import pt.ualg.Car.common.Concurrent.ReadChannel;

/**
 * Sends CarpadMessage objects to its Listeners.
 *
 * @author Joao Bispo
 */
public class CommandBroadcaster implements Runnable {

   public CommandBroadcaster(ReadChannel<CarpadMessage> channel) {
      this.channel = channel;
      this.listeners = new ArrayList<CarpadMessageListener>();
      this.run = false;
      receivedFirstMessage = false;
   }

   /**
    * Returns true if it has received the first message. If it has not already,
    * waits for a time, and if it timeouts, returns false.
    *
    * @param timeout
    * @return
    */
   public boolean waitForFirstMessage(long timeoutMillis) {
      long SLEEP_WAIT = 100;
      long startTimeNanos = System.nanoTime();
      long timeoutNanos = TimeUtils.millisToNanos(timeoutMillis);

      while (!hasReceivedFirstMessage()) {
         long timePassed = System.nanoTime() - startTimeNanos;
         if(timePassed > timeoutNanos) {
            return false;
         }

         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 1.");
            Thread.currentThread().interrupt();
         }
      }
      
      return true;
   }

   public boolean hasReceivedFirstMessage() {
      return receivedFirstMessage;
   }



   @Override
   public void run() {
      run = true;

      // Try to receive the first message
      CarpadMessage command = null;
      boolean isCommandNull = command == null;
      while(isCommandNull & run) {
         command = processCommand();
         isCommandNull = command == null;
      }

      receivedFirstMessage = true;

      
      while(run) {
         processCommand();
      }
   }


   private CarpadMessage processCommand() {
         CarpadMessage command = null;

         // Listen to the channel, and send the command everytime one arrives.
         try {
            command = channel.poll(readTimeoutInMillis, TimeUnit.MILLISECONDS);
         } catch (InterruptedException ex) {
            if(run) {
               logger.warning("Thread was interrupted without shuting down CommandBroadcaster first.");
            }
            Thread.currentThread().interrupt();
         }

         // Send the command to all listeners
         if(command != null) {
            for(CarpadMessageListener listener : listeners) {
               listener.processMessage(command);
            }
         }

         return command;
   }

   /**
    * Adds a listener to this CommandBroadcaster.
    *
    * @param listener
    */
   public void addListener(CarpadMessageListener listener) {
      listeners.add(listener);
   }

   public void shutdown() {
      run = false;
   }

   /**
    * INSTANCE VARIABLES
    */
   private ReadChannel<CarpadMessage> channel;
   private List<CarpadMessageListener> listeners;
   private boolean run;
   private boolean receivedFirstMessage;

   private static final long readTimeoutInMillis = 1000;

   private Logger logger = Logger.getLogger(CommandBroadcaster.class.getName());


}
