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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import javax.swing.JDialog;
import pt.ualg.Car.Controller.CarpadController;
import pt.ualg.Car.Controller.CarpadState;
import pt.ualg.Car.JavaDriver.GUI.DriverModel;
import pt.ualg.Car.JavaDriver.GUI.GuiAction;
import pt.ualg.Car.JavaDriver.GUI.GuiListener;
import pt.ualg.Car.System.CommandBroadcaster;

/**
 *
 * @author Joao Bispo
 */
public class Main implements Runnable, GuiListener {

   public Main(String carpadPortName) {
      this.carpadPortName = carpadPortName;
      signalCarpadDisconnected = false;
      mainState = null;
      carpad = null;
      messageQueue = new ArrayBlockingQueue<GuiAction>(1);
   }

   @Override
   public void run() {
      // Start the GUI
      driverModel = new DriverModel();
      driverModel.init();
      driverModel.addListener(this);

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



      while(true) {
         /*
         if(mainState == MainState.CONNECTED) {
            detachCarpad();
            driverModel.updateDriverScreenMessage("Disconnected.");
         }
          */

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

         /*
         // Check if Carpad got disconnected
         if(signalCarpadDisconnected) {
            detachCarpad();
            signalCarpadDisconnected = false;
            driverModel.updateDriverScreenMessage("Lost connection with Carpad Controller.");
         }
          */

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
      //boolean isConnected = connectToCarpad();

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

      // Create CarpadController
      carpad = new CarpadController(carpadPortName);


      // Create Broacaster and connect to Carpad Controller
      broadcaster = new CommandBroadcaster(carpad.getReadChannel());
      // Add GUI as a listener
      broadcaster.addListener(driverModel);

      // Create Executors
      messagesExec = Executors.newSingleThreadExecutor();
      broadcasterExec = Executors.newSingleThreadExecutor();

      // Launch threads
      broadcasterExec.execute(broadcaster);

      messagesExec.execute(carpad);

      // Add code in case the connection to carpad terminates
      /*
      messagesExec.execute(new Runnable() {


         @Override
         public void run() {
            // Check state
            if (mainState == MainState.CONNECTED) {
               logger.info("Carpad Controller got disconnected.");
               signalCarpadDisconnected = true;
            }


         }
      });
       */

      // Wait for inicialization of carpad
      carpad.waitInitialization();

      if(carpad.getState() == CarpadState.TERMINATED) {
         return false;
      }

      // If Carpad could successfully connect, store carpadPortName
      String connectedCommPort = carpad.getCommPortName();
      if(connectedCommPort != null) {
         carpadPortName = connectedCommPort;
      }

      // Wait for first message of broadcaster
      return broadcaster.waitForFirstMessage(LONG_SLEEP_WAIT*3);
   }


   /**
    * Looks for CarPad port. Returns null if it is not found.
    * @return
    */
   private String findCarpadPort() {
      String carpadPort = null;
      // Find serial ports
      List<String> serialPorts = CarpadController.listSerialPorts();
      int numPorts = serialPorts.size();

      for(int i=0; i<numPorts; i++) {
         String testPort = serialPorts.get(i);
         driverModel.updateDriverScreenMessage("("+(i+1)+"/"+(numPorts)+") " +
                 "Looking at port ["+testPort+"]...");
         boolean isPortConnectable = CarpadController.testPort(testPort);

         if(isPortConnectable) {
            return testPort;
         }
      }

      return carpadPort;
   }

   private boolean connectToCarpad() {
      /*
      if(mainState != mainState.DISCONNECTED) {
         logger.warning("Could not connect because state is '"+
                 mainState+"' instead of "+mainState.DISCONNECTED+".");
         return false;
      }
      */

      // Look for controller in the given port
      driverModel.updateDriverScreenMessage("Trying to find cardpad ["+carpadPortName+"]...");
      boolean isInPort = CarpadController.testPort(carpadPortName);

      if(!isInPort) {
         carpadPortName = findCarpadPort();
      }

      if(carpadPortName != null) {
         carpad = new CarpadController(carpadPortName);
      }

      if(carpad == null) {
         mainState = MainState.DISCONNECTED;
         driverModel.updateDriverScreenMessage("Could not connect.");
         return false;
      } else {
         mainState = MainState.CONNECTED;
         driverModel.updateDriverScreenMessage("Found CarPad! ["+carpadPortName+"]");
         return true;
      }
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
   private CarpadController carpad;
   // Thread that passes the Controller Messages to the Broadcaster.
   // Executes the interface between the CarPad Controller and the Broadcaster
   private ExecutorService messagesExec;

    // Broadcasts Controller Messages to whom it may concern (GUI and Car)
   private CommandBroadcaster broadcaster;
   // Executes the Broadcasts in another Thread
   private ExecutorService broadcasterExec;

   // The GUI
   private DriverModel driverModel;
   private BlockingQueue<GuiAction> messageQueue;
   //private ConcurrentLinkedQueue<GuiAction> messageQueue;

   // Utils
   private Logger logger = Logger.getLogger(Main.class.getName());

   // Error During Runtime
   private boolean signalCarpadDisconnected;




}
