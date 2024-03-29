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

import pt.ualg.Car.JavaDriver.Controller.CarpadModule;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import pt.ualg.Car.Controller.CarpadControllerPort;
import pt.ualg.Car.Controller.CarpadState;
import pt.ualg.Car.JavaDriver.GUI.DriverModel;
import pt.ualg.Car.JavaDriver.GUI.GuiAction;
import pt.ualg.Car.JavaDriver.GUI.GuiListener;
import pt.ualg.Car.JavaDriver.System.CommandToKeyboard;
import pt.ualg.Car.System.CommandBroadcaster;
import pt.ualg.Car.common.PrefUtils;

/**
 *
 * @author Joao Bispo
 */
public class Main implements Runnable, GuiListener {

   public Main() {
      carpadModule = new CarpadModule();

      // BEGIN_DONE
      // Get carpadPortName - Using the preferences method so it can return null
      // if not found.
      String commPortName = prefs.get(PrefCarpad.CommPortNameString.name(), null);
      if(commPortName == null) {
         commPortName = CarpadControllerPort.defaultCommPortName();
      }
      
      this.carpadPortName = commPortName;
      // END_DONE

      mainState = null;
      carpad = null;
      messageQueue = new ArrayBlockingQueue<GuiAction>(1);
   }

   /**
    * Runs the program.
    *
    * <p> Note: the instance variable "mainState" can only be changed in this
    * thread. Any methods which alter this variable can only be called during
    * this thread.
    */
   @Override
   public void run() {
      // Start the GUI
      driverModel = new DriverModel();
      driverModel.init();
      driverModel.addListener(this);

      // Initialize Program
      initProgram();

      while(true) {

         // Check if Carpad got disconnected
         boolean carpadTerminated = carpad.getState() == CarpadState.TERMINATED;
         boolean isConnected2 =  mainState == MainState.CONNECTED;
         if(carpadTerminated && isConnected2) {
            mainState = MainState.WHILE_DISCONNECTING;
            detachCarpad();
            mainState = MainState.DISCONNECTED;
            driverModel.setConnectButtonText("Connect");
            driverModel.updateDriverScreenMessage("Lost connection with Carpad Controller.");
         }


         // Process any messages from the GUI
         GuiAction action = messageQueue.poll();
         while(action != null) {
            processAction(action);
            action = messageQueue.poll();
         }

         try {
            Thread.sleep(LONG_SLEEP_WAIT);
         } catch (InterruptedException ex) {
            logger.warning("Thread Interrupted.");
         }
      }

   }

   /**
    * Steps for initiallizing the program. After this, the program can be
    * either in CONNECTED or DISCONNECTED state.
    */
   private void initProgram() {
      // Put the program in WHILE_CONNECTING state
      mainState = MainState.WHILE_CONNECTING;
      // Try to connect to carpad
      boolean isConnected = attachCarpad();

      if(isConnected) {
         mainState = MainState.CONNECTED;
         driverModel.updateDriverScreenMessage("Connected!");
         driverModel.setConnectButtonText("Disconnect");
      } else {
         mainState = MainState.DISCONNECTED;
         driverModel.updateDriverScreenMessage("Could not Connect.");
         driverModel.setConnectButtonText("Connect");
      }
      driverModel.activateConnectButton(true);
   }

   /**
    * Tries to go from the state CONNECTED to DISCONNECTED.
    */
   private void detachCarpad() {
      if(mainState != mainState.WHILE_DISCONNECTING) {
         logger.warning("Could not detach Carpad because state is '"+
                 mainState+"' instead of "+mainState.WHILE_DISCONNECTING+".");
         return;
      }

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
   }

   /**
    * Tries to go from the state DISCONNECTED to CONNECTED
    *
    * @return true if it could sucessfully attach the Carpad Controller. False
    * otherwise.
    */
   private boolean attachCarpad() {
      if(mainState != mainState.WHILE_CONNECTING) {
         logger.warning("Could not attach carpad because state is '"+
                 mainState+"' instead of "+mainState.WHILE_CONNECTING+".");
         return false;
      }

      // Create CarpadControllerPort
      carpad = new CarpadControllerPort(carpadPortName);

      // Create translator
      commandToKeyboard = new CommandToKeyboard();

      // Create Broacaster and connect to Carpad Controller
      broadcaster = new CommandBroadcaster(carpad.getReadChannel());

      // Add GUI as a listener
      broadcaster.addListener(driverModel);
      // Add translator as a listener
      broadcaster.addListener(commandToKeyboard);

      // Create Executors
      messagesExec = Executors.newSingleThreadExecutor();
      broadcasterExec = Executors.newSingleThreadExecutor();

      // Launch threads
      broadcasterExec.execute(broadcaster);

      messagesExec.execute(carpad);


      // Wait for inicialization of carpad
      carpad.waitInitialization();

      if(carpad.getState() == CarpadState.TERMINATED) {
         return false;
      }

      // If Carpad could successfully connect, store carpadPortName
      String connectedCommPort = carpad.getCommPortName();
      if(connectedCommPort != null) {
         carpadPortName = connectedCommPort;
         PrefUtils.putPref(prefs, PrefCarpad.CommPortNameString, carpadPortName);
      }

      // Wait for first message of broadcaster
      return broadcaster.waitForFirstMessage(LONG_SLEEP_WAIT*3);
   }



   @Override
   public void processMessage(GuiAction message) {

      // Add action to message queue
      boolean hasInserted = messageQueue.offer(message);
      if(!hasInserted) {
         logger.warning("GuiAction '"+message+"' ignored.");
      }
   }


   private void processAction(GuiAction guiAction) {
      switch(guiAction) {
         case CONNECT_BUTTON:
            connectAction();
            break;
      }
   }

   /**
    * Connect Button was clicked. If state is connected, disconnect,
    * and vice-versa.
    */
   private void connectAction() {
      // If Connected, Disconnect
      if(mainState == MainState.CONNECTED) {
         // Disable button
         driverModel.activateConnectButton(false);
         mainState = MainState.WHILE_DISCONNECTING;
         driverModel.updateDriverScreenMessage("Disconnecting...");
         detachCarpad();

         // Now it is disconnected
         mainState = MainState.DISCONNECTED;
         driverModel.updateDriverScreenMessage("Disconnected.");
         driverModel.activateConnectButton(true);
         driverModel.setConnectButtonText("Connect");
      } else if(mainState == MainState.DISCONNECTED) {
         // Disable button
         driverModel.activateConnectButton(false);
         mainState = MainState.WHILE_CONNECTING;
         driverModel.updateDriverScreenMessage("Connecting...");
         boolean isConnected = attachCarpad();

         if(isConnected) {
            // Now it is connected
            mainState = MainState.CONNECTED;
            driverModel.updateDriverScreenMessage("Connected.");
            driverModel.activateConnectButton(true);
            driverModel.setConnectButtonText("Disconnect");
         }
         else {
            mainState = MainState.DISCONNECTED;
            driverModel.updateDriverScreenMessage("Could not connect.");
            driverModel.activateConnectButton(true);
         }
      }
   }


   /**
    * INSTANCE VARIABLES
    */
   // State
   private String carpadPortName;
   private MainState mainState;

   private final static long SLEEP_WAIT = 100;
   private final static long LONG_SLEEP_WAIT = 1000;

   // Carpad controller
   private CarpadControllerPort carpad;
   // Thread that passes the Controller Messages to the Broadcaster.
   // Executes the interface between the CarPad Controller and the Broadcaster
   private ExecutorService messagesExec;

   private CarpadModule carpadModule;

    // Broadcasts Controller Messages to whom it may concern (GUI and Car)
   private CommandBroadcaster broadcaster;
   // Executes the Broadcasts in another Thread
   private ExecutorService broadcasterExec;

   // Translates the Carpad Messages into Keypresses
   private CommandToKeyboard commandToKeyboard;

   // The GUI
   private DriverModel driverModel;
   private BlockingQueue<GuiAction> messageQueue;

   // Utils
   private Logger logger = Logger.getLogger(Main.class.getName());
   private Preferences prefs = PrefCarpad.getPreferences();





}
