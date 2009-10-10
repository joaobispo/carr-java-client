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

package pt.ualg.Car.JavaDriver.System;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.ControllerMessage;
import pt.ualg.Car.Controller.ControllerMessageListener;

/**
 *
 * @author Joao Bispo
 */
public class CommandToKeyboard implements ControllerMessageListener {

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
   }

   @Override
   public void processMessage(ControllerMessage message) {
      // Look at Wheel value
      int wheelValue = message.getAngles()[DriverInput.WHEEL.getControllerInputIndex()];
      processWheel(wheelValue);

      int triggerValue = message.getAngles()[DriverInput.TRIGGER.getControllerInputIndex()];
      processTrigger(triggerValue);
     
   }

   private void processWheel(int wheelValue) {

      // Get Wheel position
      Position pos = getWheelPosition(wheelValue);

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
      Position pos = getTriggerPosition(triggerValue);

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


   private Position getWheelPosition(int angle) {
      if (angle < (wheelNeutralValue - wheelSensitivity)) {
         return Position.UP;
      } else if (angle > (wheelNeutralValue + wheelSensitivity)) {
         return Position.DOWN;
      } else {
         return Position.NEUTRAL;
      }
   }

   private Position getTriggerPosition(int angle) {
      if (angle > (triggerNeutralValue + triggerSensitivity)) {
         return Position.DOWN;
      } else if (angle < (triggerNeutralValue - triggerSensitivity)) {
         return Position.UP;
      } else {
         return Position.NEUTRAL;
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private boolean isJPressed;
   private boolean isLPressed;

   private boolean isAPressed;
   private boolean isZPressed;

   private Robot robot;

   private int wheelUp = KeyEvent.VK_L;
   private int wheelDown = KeyEvent.VK_J;
   private int triggerUp = KeyEvent.VK_A;
   private int triggerDown = KeyEvent.VK_Z;

   private int wheelNeutralValue = 93;
   private int triggerNeutralValue = 86;
   private int wheelSensitivity = 3;
   private int triggerSensitivity = 4;



   enum Position {

      UP,
      DOWN,
      NEUTRAL;

   }
}
