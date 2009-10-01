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

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;
import pt.amaze.ASL.TimeUtils;
import pt.ualg.carr.client1.Command;

/**
 * Periodically, sends commands.
 *
 * @author Joao Bispo
 */
public class KeyboardController implements Runnable {

   public KeyboardController(BlockingQueue<Command> inputChannel, 
           BlockingQueue<Command> outputChannel, int periodInMillis) {
      this.inputChannel = inputChannel;
      this.outputChannel = outputChannel;
      run = false;
      packetCounter = 0;
      this.periodInMillis = periodInMillis;
      lastCommand = new Command(0, new int[Command.NUM_PORTS]);
   }


   @Override
   public void run() {
      run = true;
      while (run) {
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

   private void processCommand() {


      // Check if there is a new command to send
      if (inputChannel.size() != 0) {
         lastCommand = inputChannel.remove();
      }

      // Build the Command
      lastCommand = new Command(packetCounter, lastCommand.getAngles());
      packetCounter++;
      // Try to put object in queue
      boolean success = outputChannel.offer(lastCommand);


      if (!success) {
         logger.info("Dropped command " + lastCommand.getCounter());
      }
   }

   /**
    * Stops the reading of CarPad input signals
    */
   public void shutdown() {
      run = false;
   }


   /**
    * INSTANCE VARIABLES
    */
   private static final Logger logger = Logger.getLogger(KeyboardController.class.getName());
   // Signal sent by CarPad indicating start of a package.
   private static final int COMMAND_START = 255;
   // Indicates if the object should run or not.
   private boolean run;
   // Number to be send in the generated command packet
   private int packetCounter;
   // Queue that will receive the Commands from MainScren.
   private BlockingQueue<Command> inputChannel;
   // Queue from where the commands will be sent, at a periodic rate.
   private BlockingQueue<Command> outputChannel;
   // Rate at which command should be sent
   private long periodInMillis;
   // Last Command sent
   private Command lastCommand;



}
