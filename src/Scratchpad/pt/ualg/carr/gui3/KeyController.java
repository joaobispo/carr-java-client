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

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.logging.Logger;
import pt.ualg.Car.common.Concurrent.ReadChannel;
import pt.ualg.Car.common.Concurrent.WriteChannel;
import pt.ualg.Car.common.GuiUtils;
import pt.ualg.carr.gui3.Command.INPUT;

/**
 * Emulates the Car Controller with the Keyboard.
 *
 * @author Joao Bispo
 */
public class KeyController extends KeyAdapter {

   public KeyController() {
      angles = new int[Command.NUM_PORTS];

      // Initialize Command Channel
      int channelCapacity = 1;
      keyboadValues = new WriteChannel<int[]>(channelCapacity);
      sendValues();
   }

   /**
    * If the channel is empty, put the current command there.
    */
   private void sendValues() {
      // Run on the EDT so changes and reads to these values are sequencialized.
      GuiUtils.runOnEdt(new Runnable() {

         @Override
         public void run() {
            int[] portValuesCopy = Arrays.copyOf(angles, angles.length);
            boolean couldSend = keyboadValues.offer(portValuesCopy);
            /*
            if(!couldSend) {
               logger.info("Keyboard Controller couldn't send values.");
            }
             */
         }
      });
   }

   /**
    * Get a Channel for reading inputs from Keyboard
    * @return
    */
  public ReadChannel<int[]> getKeyboadValuesReader() {
      return keyboadValues.getReadChannel();
   }


   @Override
   public void keyPressed(KeyEvent e) {
      int keyCode = e.getKeyCode();
      boolean changes = false;

      switch(keyCode) {
         case KeyEvent.VK_Q:
            changeAngle(INPUT.WHEEL, Direction.UP);
            changes = true;
            break;

         case KeyEvent.VK_A:
            changeAngle(INPUT.WHEEL, Direction.DOWN);
            changes = true;
            break;
      }

      // If there was changes, send values
      if(changes) {
         sendValues();
      }

   }

   /**
    * Changes the value of the angles.
    *
    * @param i
    * @param direction
    */
   private void changeAngle(final INPUT input, final Direction direction) {

      // Run on the EDT so changes and reads to these values are sequencialized.
      GuiUtils.runOnEdt(new Runnable() {

         @Override
         public void run() {
            // Calculate the amount to change
            int change = unitaryIncrement;
            // Direction
            if (direction.equals(Direction.DOWN)) {
               change *= -1;
            }
            // Check the value of the array
            int value = angles[input.ordinal()];
            // Calculate the new value
            angles[input.ordinal()] = calculateAngle(value, change, input);
         }
      });




   }

   /**
    * Calculates the value the angle will have. Some rume applies:
    * 
    * <p> TRIGGER and WHEEL values only change between 44 and 134;
    * <p> All the others change between 0 and 179.
    * 
    * @param value
    * @param change
    */
   private int calculateAngle(int value, int change, INPUT input) {
      //TODO Implement rules here, and init values on Input
      int tempValue = value + change;

      // Check ranges
      if(tempValue < 0) {
         return 0;
      }

      if(tempValue > 179) {
         return 179;
      }

      return tempValue;
   }

   /**
    * INSTANCE VARIABLES
    */
   private int[] angles;
   private int unitaryIncrement = 1;
   private WriteChannel<int[]> keyboadValues;

   // Utils
   private Logger logger = Logger.getLogger(KeyController.class.getName());




   enum Direction {
      UP,
      DOWN;
   }
}
