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

package pt.ualg.carr;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.awt.Window;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ualg.carr.client1.CarPadInput;
import pt.ualg.carr.client1.Command;
import pt.ualg.carr.client1.ListenerExample;
import pt.ualg.carr.client1.SignalGenerator;
import pt.ualg.carr.gui.MainWindow;
import pt.ualg.carr.gui2.MainScreen;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class TestMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

       //testSerialComm();
       //testCarPadInputOnly();

       //testCarPadInput();
       testGui2();
    }

   public static void testSerialComm() {
      SerialCommunication comm = new SerialCommunication();
      try {
         comm.run();
      } catch (Exception ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   private static void testCarPadInput() {
      String commPortName = "COM4";

      /*
      CommPortIdentifier commPortId = null;
      try {
         commPortId = CommPortIdentifier.getPortIdentifier(commPortName);
      } catch (NoSuchPortException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      SerialPort port = null;
      try {
         port = (SerialPort) commPortId.open("Car Command", 4000);
      } catch (PortInUseException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }
       */

      // Create BlockingQueue
      BlockingQueue<Command> channel = new ArrayBlockingQueue<Command>(1);


      // Create carPad
      CarPadInput carPad = new CarPadInput(channel, commPortName);
      ExecutorService carPadExecutor = Executors.newSingleThreadExecutor();      

      // Create SignalGenerator
      SignalGenerator signalGen = new SignalGenerator(channel);
      ExecutorService signalExecutor = Executors.newSingleThreadExecutor();
      

      MainWindow mainWindow = new MainWindow();
      signalGen.addListener(mainWindow);

      // Lauch Threads
      signalExecutor.execute(signalGen);
      carPadExecutor.execute(carPad);

      mainWindow.setVisible(true);


      /*
      try {
         // Sleep for a while
         Thread.sleep(3000);
      } catch (InterruptedException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
         Thread.currentThread().interrupt();
      }
       */
      

      // Stop carPad
      /*
      carPad.shutdown();
      signalGen.shutdown();
      carPadExecutor.shutdown();
      signalExecutor.shutdown();
      mainWindow.dispose();
*/
   }

   private static void testCarPadInputOnly() {
String commPortName = "COM4";

      CommPortIdentifier commPortId = null;
      try {
         commPortId = CommPortIdentifier.getPortIdentifier(commPortName);
      } catch (NoSuchPortException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      SerialPort port = null;
      try {
         port = (SerialPort) commPortId.open("Car Command", 4000);
      } catch (PortInUseException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Create BlockingQueue
      BlockingQueue<Command> channel = new ArrayBlockingQueue<Command>(1);


      // Create carPad
      CarPadInput carPad = new CarPadInput(channel, commPortName);
      ExecutorService carPadExecutor = Executors.newSingleThreadExecutor();

      carPadExecutor.execute(carPad);

      while(true) {
         try {
            Command command = channel.take();
         } catch (InterruptedException ex) {
            Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
         }
         
      }
   }

   private static void testGui2() {
      MainScreen mainScreen = new MainScreen(Command.NUM_PORTS);
      mainScreen.setVisible();
   }

}
