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
import java.util.List;
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
import pt.ualg.carr.gui2.CommandBroadcaster;
import pt.ualg.carr.gui.MainWindow;
import pt.ualg.carr.gui2.ControllerSerialPort;
import pt.ualg.carr.gui2.MainProgram;
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
       //testGui2();
       //testProgramV1();
       //testInputInterruption();
       testControllerSerial();
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

      // Create CommandBroadcaster
      CommandBroadcaster signalGen = new CommandBroadcaster(channel);
      ExecutorService signalExecutor = Executors.newSingleThreadExecutor();
      

      //MainWindow mainWindow = new MainWindow();
      //signalGen.addListener(mainWindow);

      MainScreen mainScreen = new MainScreen(Command.NUM_PORTS);
      signalGen.addListener(mainScreen);

      // Lauch Threads
      signalExecutor.execute(signalGen);
      carPadExecutor.execute(carPad);

      //mainWindow.setVisible(true);
      mainScreen.setVisible();

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

      // Create Screen
      MainScreen mainScreen = new MainScreen(Command.NUM_PORTS);
      mainScreen.setVisible();

      // Create BlockingQueue
      final BlockingQueue<Command> channel = new ArrayBlockingQueue<Command>(1);

      // Create CommandBroadcaster
      CommandBroadcaster signalGen = new CommandBroadcaster(channel);
      signalGen.addListener(mainScreen);

      // Create carPad
      final CarPadInput carPad = new CarPadInput(channel, "COM4");

      // Executor
      ExecutorService signalExecutor = Executors.newSingleThreadExecutor();
      signalExecutor.execute(signalGen);

      final ExecutorService carPadExecutor = Executors.newSingleThreadExecutor();
      carPadExecutor.execute(carPad);

      carPadExecutor.execute(new Runnable() {

         @Override
         public void run() {
            // Flush the channel
            while (!channel.isEmpty()) {
               channel.remove();
            }

            // Schedule CarInput Again
            carPadExecutor.execute(carPad);
            carPadExecutor.execute(this);
         }
      });

      try {
         Thread.sleep(100000);
      } catch (InterruptedException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      mainScreen.dispose();
      signalGen.shutdown();
      carPad.shutdown();

      carPadExecutor.shutdownNow();
      signalExecutor.shutdownNow();
      


      // Flush the channel
      while(!channel.isEmpty()) {
         channel.remove();
      }

   }

   private static void testProgramV1() {
      MainProgram mainProgram = new MainProgram();
      mainProgram.runV1();
   }

   private static void testInputInterruption() {
      // Create Screen
      MainScreen mainScreen = new MainScreen(Command.NUM_PORTS);
      mainScreen.setVisible();

      // Create BlockingQueue
      final BlockingQueue<Command> channel = new ArrayBlockingQueue<Command>(1);

      // Create CommandBroadcaster
      CommandBroadcaster signalGen = new CommandBroadcaster(channel);
      signalGen.addListener(mainScreen);

      // Create carPad
      final CarPadInput carPad = new CarPadInput(channel, "COM4");

      // Executors
      ExecutorService signalExecutor = Executors.newSingleThreadExecutor();
      ExecutorService carPadExecutor = Executors.newSingleThreadExecutor();
      
      // Launch
      signalExecutor.execute(signalGen);
      carPadExecutor.execute(carPad);
      
      // Wait a bit, to receive some inputs
      try {
         Thread.sleep(3000);
      } catch (InterruptedException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Interrupt Connection
      carPad.setChannel(new ArrayBlockingQueue<Command>(1));

      // Wait a bit, with connection interrupted
      try {
         Thread.sleep(2000);
      } catch (InterruptedException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Connect again
      carPad.setChannel(channel);
   }

   private static void testControllerSerial() {
      System.out.println("Found ports: "+ControllerSerialPort.listSerialPorts());

      String comPort = ControllerSerialPort.findCarController();

      /*
      SerialPort serialPort = ControllerSerialPort.connectSerial("COM3", "Testing Ports");

      if(serialPort != null) {
         System.out.println("Port could be opened!");
         serialPort.close();
      }

      SerialPort serialPort2 = ControllerSerialPort.connectSerial("COM3", "Testing Ports");

      if(serialPort2 != null) {
         System.out.println("Port could be opened again!");
         serialPort2.close();
      }
       */

      
   }

}