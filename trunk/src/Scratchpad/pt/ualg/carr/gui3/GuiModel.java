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

import java.util.Arrays;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.ControllerInput;
import pt.ualg.Car.common.Concurrent.ReadChannel;
import pt.ualg.Car.common.Concurrent.WriteChannel;
import pt.ualg.Car.common.GuiUtils;


/**
 *
 * @author Joao Bispo
 */
public class GuiModel implements ValuesListener {

   public GuiModel() {
      int numPorts = ControllerInput.values().length;
      portValues = new int[numPorts];
      mainScreen = new MainScreen();
      // Initialize Command Channel
      //int channelCapacity = 1;
      //keyboadValues = new WriteChannel<int[]>(channelCapacity);
      //sendValues();
   }



   /**
    * Initialize GUI
    */
   public void init() {
      // Init Windows Components
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            mainScreen.initComponents();
            mainScreen.getWindowFrame().setVisible(true);
         }
      }
      );
      
   }

   public void attachKeyboard(final KeyController controller) {

      // Attatch Keyboard Listener
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            mainScreen.attachKeyboard(controller);
         }
      }
      );
   }

   public void detachKeyboard(final KeyController controller) {

      // Attatch Keyboard Listener
      GuiUtils.runOnEdt(new Runnable() {
         @Override
         public void run() {
            mainScreen.detachKeyboard(controller);
         }
      }
      );
   }


   /**
    * If the channel is empty, put the current command there.
    */
   /*
   private void sendValues() {
      // Run on the EDT so changes and reads to these values are sequencialized.
      GuiUtils.runOnEdt(new Runnable() {

         @Override
         public void run() {
            int[] portValuesCopy = Arrays.copyOf(portValues, portValues.length);
            boolean couldSend = keyboadValues.offer(portValuesCopy);
            if(!couldSend) {
               logger.info("Gui couldn't send values.");
            }
         }
      });
   }
    */

   /*
   public ReadChannel<int[]> getKeyboadValuesReader() {
      return keyboadValues.getReadChannel();
   }
    */

   @Override
   public void processValues(final int[] values) {
      // Run on the EDT so changes and reads to these values are sequencialized.
      GuiUtils.runOnEdt(new Runnable() {

         @Override
         public void run() {
            mainScreen.updateTextFields(values);
         }
      });
   }


   /**
    * INSTANCE VARIABLES
    */
   // State
   private int[] portValues;
   //private WriteChannel<int[]> keyboadValues;

   // Windows
   private MainScreen mainScreen;

   // Utils
   private Logger logger = Logger.getLogger(GuiModel.class.getName());




}
