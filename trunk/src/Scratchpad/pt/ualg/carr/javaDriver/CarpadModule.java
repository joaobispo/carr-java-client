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

package pt.ualg.carr.javaDriver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.CarpadMessage;
import pt.ualg.Car.JavaDriver.Controller.CarpadPort;
import pt.ualg.Car.common.Concurrent.WriteChannel;

/**
 * Attempt to do an "assynchronous" Runnable
 *
 * @author Joao Bispo
 */
public class CarpadModule {

   public CarpadModule() {
      carpadExec = Executors.newSingleThreadExecutor();
      channel = new WriteChannel<CarpadMessage>(1);

      carpadPort = null;
   }

   //start()

   //waitUntilStart() ()
   /**
    * After calling the method start(), this method blocks until the method
    * ends.
    *
    * @return
    */
   public boolean waitForStart() {
      return false;
   }

   /**
    * INSTANCE VARIABLES
    */
   // Runs the thread of this object
   private final ExecutorService carpadExec;
   // Channel to where the commands will be sent.
   private final WriteChannel<CarpadMessage> channel;

   // Carpad controller
   private CarpadPort carpadPort;

   // Utils
   private Logger logger = Logger.getLogger(CarpadModule.class.getName());
}
