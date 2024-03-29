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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.CarpadMessage;
import pt.ualg.Car.common.PrefUtils;
import pt.ualg.Car.JavaDriver.PrefCarpad;
import pt.ualg.Car.common.Concurrent.ReadChannel;
import pt.ualg.Car.common.Concurrent.WriteChannel;

/**
 * Runs and manages the CarpadController.
 *
 * @author Joao Bispo
 */
public class CarpadModule {

   public CarpadModule() {
      state = CarpadState.INITIALIZING;
      carpadExec = Executors.newSingleThreadExecutor();
      carpad = new CarpadPort();
   }

   /**
    *
    * @return ReadChannel where messages from Carpad are written.
    */
   public ReadChannel getReadChannel() {
      return channel.getReadChannel();
   }

   public boolean activate() {
      return false;
   }

   class Executable implements Runnable {

      @Override
      public void run() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

   }

   /**
    * INSTANCE VARIABLES
    */
   // Channel to where the commands will be sent.
   private WriteChannel<CarpadMessage> channel;
   // Carpad controller
   private CarpadPort carpad;
   // Thread that runs the CarpadController and generates the Controller Messages.
   // Executes the interface between the CarPad Controller and the message queue.
   private ExecutorService carpadExec;



   private CarpadState state;


   // Utils
   private Logger logger = Logger.getLogger(CarpadModule.class.getName());
   private PrefUtils prefs = PrefCarpad.getPrefUtils();

   enum CarpadState {
      INITIALIZING,
      RUNNING,
      TERMINATED;
   }
}
