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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import pt.ualg.Car.Controller.CarpadController;
import pt.ualg.Car.JavaDriver.GUI.DriverModel;
import pt.ualg.Car.System.CommandBroadcaster;
import pt.ualg.Car.common.GuiUtils;

/**
 *
 * @author Joao Bispo
 */
public class Main implements Runnable {

   public Main(long periodInMillis, String carpadPortName) {
      this.periodInMillis = periodInMillis;
      this.carpadPortName = carpadPortName;
      carpadGotDisconnected = false;
      mainState = MainState.IDLE;
      carpad = null;
   }

   @Override
   public void run() {
      // Start the GUI
      driverModel = new DriverModel();
      driverModel.init();


      // Look for controller in the given port
      driverModel.updateDriverScreenMessage("Trying to connect to port ["+carpadPortName+"]...");
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
      } else {
         mainState = MainState.CONNECTED;
         driverModel.updateDriverScreenMessage("Connect at port ["+carpadPortName+"]!");
      }

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



   /**
    * INSTANCE VARIABLES
    */
   // State
   private long periodInMillis;
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

   // Utils
   private Logger logger = Logger.getLogger(Main.class.getName());

   // Error During Runtime
   private boolean carpadGotDisconnected;


}
