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

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ualg.carr.client1.Command;
import pt.ualg.carr.client1.CommandListener;

/**
 *
 * @author Joao Bispo
 */
public class CommandToKeyboard implements CommandListener {

   public CommandToKeyboard() {
      try {
         robot = new Robot();
      } catch (AWTException ex) {
         Logger.getLogger(CommandToKeyboard.class.getName()).log(Level.SEVERE, null, ex);
      }

      isJPressed = false;
      isLPressed = false;

      isAPressed = false;
      isZPressed = false;
      currentWheelValue = 93;
   }



   @Override
   public void processCommand(Command command) {
      // Look at Wheel value
      int wheelValue = command.getAngles()[4];
      processWheel(wheelValue);

      int triggerValue = command.getAngles()[5];
      processTrigger(triggerValue);
      // Check which position it is
      /*
      if(wheelValue < 90) {
         robot.keyPress(wheelUp);
      } else if(wheelValue > 96) {
         robot.keyPress(KeyEvent.VK_I);
      } else {
         if(currentWheelValue < 90) {
            robot.keyRelease(wheelUp);
         } else {
            robot.keyRelease(KeyEvent.VK_I);
         }
      }
      currentWheelValue = wheelValue;
*/

      /*
      // Look at Trigger value
      int triggerValue = command.getAngles()[5];

      // Check which position it is
      if(triggerValue > 90 ) {
         robot.keyPress(KeyEvent.VK_A);
      } else if(triggerValue < 82) {
         robot.keyPress(KeyEvent.VK_Z);
      }
       */
   }

   private void processWheel(int wheelValue) {

      // Get Wheel position
      Position pos = Position.getWheelPosition(wheelValue);

      // Neutral position
      if(!isJPressed && !isLPressed) {
         switch(pos) {
            case UP:
               robot.keyPress(wheelUp);
               isLPressed = true;
               break;

            case DOWN:
               robot.keyPress(wheelDown);
               isJPressed = true;
               break;
         }
      }

      // Turning left (wheel down)
      if(isJPressed && !isLPressed) {
         switch(pos) {
            case NEUTRAL:
               robot.keyRelease(wheelDown);
               isJPressed = false;
               break;

            case UP:
               robot.keyRelease(wheelDown);
               isJPressed = false;
               robot.keyPress(wheelUp);
               isLPressed = true;
               break;

         }
      }

      // Turning right (wheel up)
      if(!isJPressed && isLPressed) {
         switch(pos) {
            case NEUTRAL:
               robot.keyRelease(wheelUp);
               isLPressed = false;
               break;

            case DOWN:
               robot.keyRelease(wheelUp);
               isLPressed = false;
               robot.keyPress(wheelDown);
               isJPressed = true;
               break;

         }
      }
   }


   private void processTrigger(int triggerValue) {
      // Get Trigger position
      Position pos = Position.getTriggerPosition(triggerValue);

      // Neutral position
      if(!isAPressed && !isZPressed) {
         switch(pos) {
            case DOWN:
               robot.keyPress(triggerUp);
               isAPressed = true;
               break;

            case UP:
               robot.keyPress(triggerDown);
               isZPressed = true;
               break;
         }
      }

      // Acceleration (trigger)
      if(isAPressed && !isZPressed) {
         switch(pos) {
            case NEUTRAL:
               robot.keyRelease(triggerUp);
               isAPressed = false;
               break;

            case UP:
               robot.keyRelease(triggerUp);
               isAPressed = false;
               robot.keyPress(triggerDown);
               isZPressed = true;
               break;

         }
      }

      // Breaking (trigger invert)
      if(!isAPressed && isZPressed) {
         switch(pos) {
            case NEUTRAL:
               robot.keyRelease(triggerDown);
               isZPressed = false;
               break;

            case DOWN:
               robot.keyRelease(triggerDown);
               isZPressed = false;
               robot.keyPress(triggerUp);
               isAPressed = true;
               break;

         }
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private int currentWheelValue;
   private boolean isJPressed;
   private boolean isLPressed;

   private boolean isAPressed;
   private boolean isZPressed;

   private Robot robot;

   private int wheelUp = KeyEvent.VK_L;
   private int wheelDown = KeyEvent.VK_J;
   private int triggerUp = KeyEvent.VK_A;
   private int triggerDown = KeyEvent.VK_Z;



   enum Position {

      UP,
      DOWN,
      NEUTRAL;

      public static Position getWheelPosition(int angle) {
         if (angle < 90) {
            return UP;
         } else if (angle > 96) {
            return DOWN;
         } else {
            return NEUTRAL;
         }
      }

      public static Position getTriggerPosition(int angle) {
         if (angle > 90) {
            return DOWN;
         } else if (angle < 82) {
            return UP;
         } else {
            return NEUTRAL;
         }
      }
   }
}
