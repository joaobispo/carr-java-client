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

import java.util.ArrayList;
import java.util.List;
import pt.ualg.Car.Controller.CarpadMessage;
import pt.ualg.Car.JavaDriver.System.DriverInput;
import pt.ualg.Car.common.GuiUtils;
import pt.ualg.Car.Controller.CarpadMessageListener;

/**
 *
 * @author Joao Bispo
 */
public class DriverModel implements CarpadMessageListener {

   public DriverModel() {
      listeners = new ArrayList<GuiListener>();
      driverScreen = new DriverScreen(listeners);
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
   
   @Override
   public void processMessage(final CarpadMessage message) {
      // Run on the EDT so changes and reads to these values are sequencialized.
      GuiUtils.runOnEdt(new Runnable() {

         @Override
         public void run() {
            int[] angles = message.getAngles();
            int wheelAngle = angles[DriverInput.WHEEL.getControllerInputIndex()];
            int triggerAngle = angles[DriverInput.TRIGGER.getControllerInputIndex()];
            driverScreen.updateInputs(wheelAngle, triggerAngle);
         }
      });
   }


   public void updateDriverScreenMessage(final String message) {
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            driverScreen.updateConnectionMessage(message);
         }

      });
   }

   public void activateConnectButton(final boolean b) {
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            driverScreen.activateConnectButton(b);
         }
      });
   }

   public void setConnectButtonText(final String text) {
       GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            driverScreen.setConnectButtonText(text);
         }
      });
   }

   public void addListener(final GuiListener listener) {
       GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            listeners.add(listener);
         }
      });
   }

   /**
    * INSTANCE VARIABLES
    */

   // Windows
   private DriverScreen driverScreen;

   // Global Listeners List for this Window Application
   private List<GuiListener> listeners;


}
