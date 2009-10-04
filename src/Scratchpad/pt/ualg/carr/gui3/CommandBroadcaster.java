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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import pt.ualg.Car.common.Concurrent.ReadChannel;

/**
 * Sends Command objects to its Listeners.
 *
 * @author Joao Bispo
 */
public class CommandBroadcaster implements Runnable {

   public CommandBroadcaster(ReadChannel<Command> channel) {
      this.channel = channel;
      this.listeners = new ArrayList<ValuesListener>();
      this.run = true;
   }

   @Override
   public void run() {
      while(run) {
         Command command = null;

         // Listen to the channel, and send the command everytime one arrives.
         try {            
            command = channel.take();
         } catch (InterruptedException ex) {
            if(run) {
               logger.warning("Thread was interrupted without shuting down CommandBroadcaster first.");
            }
            Thread.currentThread().interrupt();
         }

         // Send the command to all listeners
         if(command != null) {
            for(ValuesListener listener : listeners) {
               listener.processValues(command.getAngles());
            }
         }

      }
   }

   /**
    * Adds a listener to this CommandBroadcaster.
    *
    * @param listener
    */
   public void addListener(ValuesListener listener) {
      listeners.add(listener);
   }

   public void shutdown() {
      run = false;
   }

   /**
    * INSTANCE VARIABLES
    */
   private ReadChannel<Command> channel;
   private List<ValuesListener> listeners;
   private boolean run;

   private Logger logger = Logger.getLogger(CommandBroadcaster.class.getName());

}
