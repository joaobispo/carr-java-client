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


import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import pt.amaze.ASLCandidates.Identification.ByteIdentifier;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.Command;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.CommandSource;

/**
 * Has the main loop of the program.
 * 
 * <p>Can receive commands and be controlled by other objects, 
 * through ClientCoreProxy.
 *
 * @author Joao Bispo
 */
public class ClientCore {

   public ClientCore() {
      byteId = new ByteIdentifier();

      CommandSource carpadSource = new CarpadCommandSource(byteId, CARPAD_READ_TIMEOUT_MILLIS);

      controllers = new EnumMap<ControllerDevice, CommandSource>(ControllerDevice.class);
      controllers.put(ControllerDevice.CarpadController, carpadSource);

      currentCommandSource = carpadSource;

      taskQueue = new ArrayBlockingQueue<Runnable>(TASK_CAPACITY);

      listeners = new ArrayList<ClientListener>();
   }


   /**
    * Adds a listener to the ClientCore.
    *
    * @param listener
    */
   void addListener(ClientListener listener) {
      listeners.add(listener);
   }

   public void run() {
      initProgram();

      while(true) {
         if(currentCommandSource.isConnected()) {
            Command command = currentCommandSource.readCommand();
            processCommand(command);
         } else {
            // Try to connect
            currentCommandSource.connect();
         }

         // Execute Pending Tasks
         processTasks();
      }
   }


   private void processCommand(Command command) {
      // Check if command is null
      if(command == null) {
         Logger.getLogger(ClientCore.class.getName()).
                 warning("Received 'null' Command.");
         return;
      }

      // Send command to listeners
      for(ClientListener listener : listeners) {
         listener.newCommand(command);
      }
   }


   private void processTasks() {
      // Run each of the current tasks in succession
      int numberOfTasks = taskQueue.size();
      for(int i=0; i<numberOfTasks; i++) {
         try {
            Runnable runnable = taskQueue.poll(TASK_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            if(runnable == null) {
               Logger.getLogger(ClientCore.class.getName()).
                    warning("Timeout while reading from the task queue.");
            } else {
               runnable.run();
            }
         } catch (InterruptedException ex) {
            Logger.getLogger(ClientCore.class.getName()).
                    warning("Interrupted.");
            Thread.currentThread().interrupt();
         }
      }
   }


   /**
    * Initializes some components of the program.
    */
   private void initProgram() {

   }

   /**
    * INSTANCE VARIABLES
    */
    private Map<ControllerDevice, CommandSource> controllers;
    private ByteIdentifier byteId;
    private CommandSource currentCommandSource;

    // Task List
    private BlockingQueue<Runnable> taskQueue;

    // Listeners List
    private List<ClientListener> listeners;

    //private ClientGui clientGui;

    // DEFINITIONS
    private final static long CARPAD_READ_TIMEOUT_MILLIS = 500;
    private final static long TASK_READ_TIMEOUT_MILLIS = 10;
    private final static int TASK_CAPACITY = 10;



   /**
    * INNER CLASSES
    */
   public enum ControllerDevice {
      CarpadController,
      GuiKeyboard;
   }
}
