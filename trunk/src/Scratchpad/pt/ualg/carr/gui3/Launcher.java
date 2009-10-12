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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.CarpadControllerPort;
import pt.ualg.Car.System.CommandBroadcaster;

/**
 *
 * @author Joao Bispo
 */
public class Launcher implements Runnable {

   public Launcher(long periodInMillis, String carpadPortName) {
      this.periodInMillis = periodInMillis;
      this.carpadPortName = carpadPortName;
      inputState = InputState.IDLE;
      carpadGotDisconnected = false;
   }

   /**
    * Initiallizes program
    */
   public void init() {
      // Start the GUI
      guiModel = new GuiModel();
      guiModel.init();

      //attachCarpad();
      //detachCarpad();
      //attachKeyboard();
      /*
      messagesExec = Executors.newSingleThreadExecutor();
      messagesExec.execute(new CarpadControllerPort());
      messagesExec.execute(new Runnable() {

         @Override
         public void run() {
            logger.info("Carpad Controller got disconnected. Returning to keyboard");
         }

      });
       */
   }


   @Override
   public void run() {
      init();
      attachCarpad();

      // From time to time, check if there is any problem
      while(true) {
         // Check if Carpad got disconnected
         if(carpadGotDisconnected) {
            System.out.println("Reconnecting to keyboard...");
            detachCarpad();
            attachKeyboard();
            carpadGotDisconnected = false;
            System.out.println("Successful!");
         }

         try {
            Thread.sleep(LONG_SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted.");
         }
      }
   }

   /**
    * Tries to connect to the device, in the following order:
    * <p>1. A CarPad;
    * <p>2. The keyboard;
    */
   private void connect() {

   }



   public void execute() {

      // Keyboard Controller
      keyboard = new KeyController();

      guiModel = new GuiModel();
      guiModel.init();
      guiModel.attachKeyboard(keyboard);



      arduinoEmu = new ArduinoEmulator(keyboard.getKeyboadValuesReader(), periodInMillis);

      broadcaster = new pt.ualg.Car.System.CommandBroadcaster(arduinoEmu.getReadChannel());
      broadcaster.addListener(guiModel);


      messagesExec = Executors.newSingleThreadExecutor();
      broadcasterExec = Executors.newSingleThreadExecutor();

      messagesExec.execute(arduinoEmu);
      broadcasterExec.execute(broadcaster);
   }

   public void detachKeyboard() {
      if(inputState != inputState.USING_KEYBOARD) {
         logger.warning("Could not dettach keyboard because state is '"+
                 inputState+"' instead of "+inputState.USING_KEYBOARD+".");
         return;
      }



      // Stop Broadcaster
      broadcaster.shutdown();
      broadcasterExec.shutdown();

      // Wait for broadcaster to terminate
      System.out.print("Waiting termination of Broadcaster...");
      while(!broadcasterExec.isTerminated()) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 1.");
            Thread.currentThread().interrupt();
         }
      }
      System.out.println(" Terminated!");

      // Detach keyboard listener
      guiModel.detachKeyboard(keyboard);

      // Stop Keyboard
      arduinoEmu.shutdown();
      messagesExec.shutdown();

      // Wait for broadcaster to terminate
      System.out.print("Waiting termination of Arduino Emulator...");
      while(!messagesExec.isTerminated()) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 2.");
            Thread.currentThread().interrupt();
         }
      }
      System.out.println(" Terminated!");

      // Change state
      inputState = InputState.IDLE;
   }

   public void attachKeyboard() {
      if(inputState != inputState.IDLE) {
         logger.warning("Could not attach keyboard because state is '"+
                 inputState+"' instead of "+inputState.IDLE+".");
         return;
      }

      // Create KeyboardController
      keyboard = new KeyController();

      arduinoEmu = new ArduinoEmulator(keyboard.getKeyboadValuesReader(), periodInMillis);

      broadcaster = new CommandBroadcaster(arduinoEmu.getReadChannel());
      broadcaster.addListener(guiModel);

      messagesExec = Executors.newSingleThreadExecutor();
      broadcasterExec = Executors.newSingleThreadExecutor();

      
      broadcasterExec.execute(broadcaster);
      messagesExec.execute(arduinoEmu);

      guiModel.attachKeyboard(keyboard);

      // Change State
      inputState = InputState.USING_KEYBOARD;
   }

   public void attachCarpad() {
      if(inputState != inputState.IDLE) {
         logger.warning("Could not attach carpad because state is '"+
                 inputState+"' instead of "+inputState.IDLE+".");
         return;
      }

      // Create CarpadControllerPort
      carpad = new CarpadControllerPort(carpadPortName);

      // Create Broacaster and connect to Carpad Controller
      broadcaster = new CommandBroadcaster(carpad.getReadChannel());
      // Add GUI as a listener
      broadcaster.addListener(guiModel);

      // Create Executors
      messagesExec = Executors.newSingleThreadExecutor();
      broadcasterExec = Executors.newSingleThreadExecutor();

      // Launch threads
      broadcasterExec.execute(broadcaster);
      
      messagesExec.execute(carpad);
      // Add code in case the connection to carpad terminates
     
      messagesExec.execute(new Runnable() {

         @Override
         public void run() {
            // Check state
            if(inputState == InputState.WHILE_DISCONNECTING_CARPAD) {
               return;
            }

            logger.info("Carpad Controller got disconnected.");
            carpadGotDisconnected = true;
         }

      });
      
      
      // Wait for first message of broadcaster
      while (!broadcaster.hasReceivedFirstMessage()) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 1.");
            Thread.currentThread().interrupt();
         }
      }

      // Change State
      inputState = InputState.USING_CARPAD;
   }

   public void detachCarpad() {
      if(inputState != inputState.USING_CARPAD) {
         logger.warning("Could not detach Carpad because state is '"+
                 inputState+"' instead of "+inputState.USING_CARPAD+".");
         return;
      }

      inputState = InputState.WHILE_DISCONNECTING_CARPAD;

      // Stop Carpad Controller
      carpad.shutdown();
      messagesExec.shutdown();

      // Wait for Carpad Controller to terminate
      System.out.print("Finishing communication with Carpad Controller...");
      while(!messagesExec.isTerminated()) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 2.");
            Thread.currentThread().interrupt();
         }
      }
      System.out.println(" Terminated!");

      // Stop Broadcaster
      broadcaster.shutdown();
      broadcasterExec.shutdown();

      // Wait for broadcaster to terminate
      System.out.print("Waiting termination of Broadcaster...");
      while(!broadcasterExec.isTerminated()) {
         try {
            Thread.sleep(SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted 1.");
            Thread.currentThread().interrupt();
         }
      }
      System.out.println(" Terminated!");

      

      // Change state
      inputState = InputState.IDLE;
   }


   /**
    * INSTANCE VARIABLES
    */
   // State
   private long periodInMillis;
   private String carpadPortName;
   private InputState inputState;

   private final static long SLEEP_WAIT = 100;
   private final static long LONG_SLEEP_WAIT = 1000;

   // Carpad controller
   private CarpadControllerPort carpad;

   // Keyboard controller
   private KeyController keyboard;
   // Emulator for other input devices (usually keyboard
   private ArduinoEmulator arduinoEmu;

   // Broadcasts Controller Messages to whom it may concern (GUI and Car)
   private CommandBroadcaster broadcaster;
   // Executes the Broadcasts in another Thread
   private ExecutorService broadcasterExec;

   // Thread that passes the Controller Messages to the Broadcaster.
   // It's the interface between the Input Device (Keyboard, CarPad...)
   // and the Broadcaster
   private ExecutorService messagesExec;

   // The GUI
   private GuiModel guiModel;

   // Utils
   private Logger logger = Logger.getLogger(Launcher.class.getName());

   // Error During Runtime
   private boolean carpadGotDisconnected;


   enum InputState {
      USING_KEYBOARD,
      USING_CARPAD,
      WHILE_DISCONNECTING_CARPAD,
      IDLE;

      @Override
      public String toString() {
         return name();
      }


   }
}
