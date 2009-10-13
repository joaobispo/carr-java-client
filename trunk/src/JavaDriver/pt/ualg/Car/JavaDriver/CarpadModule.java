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

package pt.ualg.Car.JavaDriver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pt.ualg.Car.Controller.CarpadControllerPort;
import pt.ualg.Car.common.PrefUtils;

/**
 * Runs and manages the CarpadController.
 *
 * @author Joao Bispo
 */
public class CarpadModule {

   public CarpadModule() {
      state = CarpadState.INITIALIZING;
      carpadExec = Executors.newSingleThreadExecutor();

   }



   /**
    * @return the name of the last stored name of the CommPort, or the default
    * CommPort name if there is none stored.
    */
   private String loadCarpadPortName() {
      Preferences preferences = PrefCarpad.getPreferences();
      String carpadPortName = preferences.get(PrefCarpad.CommPortNameString.name(), null);
      
      if(carpadPortName == null) {
         carpadPortName = CarpadControllerPort.defaultCommPortName();
      }

      return carpadPortName;
   }

   private void storeCarpadPortName(String carpadPortName) {
      prefs.putPref(PrefCarpad.CommPortNameString, carpadPortName);
   }

   /**
    * INSTANCE VARIABLES
    */
   private CarpadState state;
   // Carpad controller
   private CarpadControllerPort carpad;
   // Thread that runs the CarpadController and generates the Controller Messages.
   // Executes the interface between the CarPad Controller and the message queue.
   private ExecutorService carpadExec;

   // Utils
   private Logger logger = Logger.getLogger(CarpadModule.class.getName());
   private PrefUtils prefs = PrefCarpad.getPrefUtils();

   enum CarpadState {
      INITIALIZING,
      RUNNING,
      TERMINATED;
   }
}
