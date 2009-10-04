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

package pt.ualg.carr.gui3;

import java.util.logging.Logger;
import pt.amaze.ASL.TimeUtils;
import pt.ualg.Car.common.Concurrent.ReadChannel;
import pt.ualg.Car.common.Concurrent.WriteChannel;

/**
 *
 * @author Joao Bispo
 */
public class ArduinoEmulator implements Runnable {

   public ArduinoEmulator(ReadChannel<int[]> inputValues, long periodInMillis) {
      this.inputCommand = inputValues;
      this.outputCommand = new WriteChannel<Command>(1);
      this.periodInMillis = periodInMillis;
      this.run = false;
      this.lastValues = new int[Command.NUM_PORTS];
   }

   public void setWriteChannel(ReadChannel<int[]> writeChannel) {
      this.inputCommand = writeChannel;
   }

   public ReadChannel<Command> getReadChannel() {
      return outputCommand.getReadChannel();
   }



   @Override
   public void run() {
      run = true;
      while(run) {
         long nanosBegin = System.nanoTime();
         // Process
         processCommand();
         // Sleep until next period
         long sleepTime = periodInMillis - TimeUtils.nanosToMillis(System.nanoTime() - nanosBegin);
         if(sleepTime > 0) {
            try {
               Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
               logger.warning("Thread Interrupted.");
               Thread.currentThread().interrupt();
            }
         }
      }
   }


   /**
    * Check the input channel for a new command. If there is no new command,
    * resends the last one.
    */
   private void processCommand() {
      int[] newValues = inputCommand.poll();

      // If queue was empty, use last command
      if(newValues == null) {
         newValues = lastValues;
      }
      // Else, update the value of last command
      else {
         lastValues = newValues;
      }

      // Try to put the command in the output queue
      Command command = new Command(newValues);
      boolean couldSend = outputCommand.offer(command);
      if (!couldSend) {
         logger.info("Arduino Emulator dropped command '" + command.getCounter() + "'.");
      }

   }

   /**
    * Stops the reading and sending of command.
    */
   public void shutdown() {
      run = false;
   }

   /**
    * INSTANCE VARIABLES
    */
   // State
   private ReadChannel<int[]> inputCommand;
   private WriteChannel<Command> outputCommand;
   private long periodInMillis;
   private boolean run;
   private int[] lastValues;

   // Utils
   private Logger logger = Logger.getLogger(ArduinoEmulator.class.getName());



}
