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

package pt.ualg.Car.JavaDriver.GUI;

import pt.ualg.Car.JavaDriver.System.JavaDriverInput;
import pt.ualg.Car.common.GuiUtils;

/**
 *
 * @author Joao Bispo
 */
public class DriverModel {

   public DriverModel() {
      int numPorts = JavaDriverInput.numberOfInputs();
      driverScreen = new DriverScreen();
   }

   /**
    * Initialize GUI
    */
   public void init() {
      // Init Windows Components
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            driverScreen.initComponents();
            driverScreen.getWindowFrame().setVisible(true);
         }
      }
      );

   }

   public void updateDriverScreenMessage(final String message) {
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            driverScreen.updateConnectionMessage(message);
         }

      });
   }

   /**
    * INSTANCE VARIABLES
    */
   // State
   //private int[] portValues;
   //private DriverState driverState;

   // Windows
   private DriverScreen driverScreen;
}
