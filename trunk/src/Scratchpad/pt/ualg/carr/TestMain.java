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

import com.fluendo.player.Cortado;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.JPanel;
import pt.amaze.ASL.DataManagement.DataMap;
import pt.amaze.ASL.LoggingUtils;
import pt.ualg.Car.Controller.CarpadControllerPort;
import pt.ualg.Car.Controller.CarpadControllerProtos.CarpadControllerData;
import pt.ualg.Car.JavaDriver.Main;
import pt.ualg.Car.JavaDriver.PrefCarpad;
import pt.ualg.Car.common.LoggingUtils2;
import pt.ualg.carr.client1.CarPadInput;
import pt.ualg.carr.client1.Command;
import pt.ualg.carr.gui2.CommandBroadcaster;
import pt.ualg.carr.gui2.CarpadControllerPortGui2;
import pt.ualg.carr.gui2.CommandToKeyboard;
import pt.ualg.carr.gui2.KeyboardController;
import pt.ualg.carr.gui2.MainProgram;
import pt.ualg.carr.gui2.MainScreen;
import pt.ualg.carr.gui3.Launcher;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class TestMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

       LoggingUtils.setupConsoleOnly();
       LoggingUtils2.redirectSystemOut();
       LoggingUtils2.redirectSystemErr();

        testLibraryExists();

        //testKeyMappings();
       //testSerialComm();
       //testCarPadInputOnly();

       //testCarPadInput();

        testGui2();

       //testProgramV1();
       //testInputInterruption();

       // testControllerSerial();
      //testKeybController();

        //attachDetachKeyboard();

        //testGui3();
        //testJavaDriver();
        //testJavaDriverWithConfig();
        //testPreferences();

        //testCortado();
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
      System.out.println("Found ports: "+CarpadControllerPortGui2.listSerialPorts());

      // Create Screen
      MainScreen mainScreen = new MainScreen(Command.NUM_PORTS);
      mainScreen.setVisible();

      // Create BlockingQueue
      final BlockingQueue<Command> channel = new ArrayBlockingQueue<Command>(1);

      // Create CommandBroadcaster
      CommandBroadcaster signalGen = new CommandBroadcaster(channel);
      signalGen.addListener(mainScreen);

      // Create "Robot"
      CommandToKeyboard robot = new CommandToKeyboard();
      signalGen.addListener(robot);

      // Create carPad
      //final CarPadInput carPad = new CarPadInput(channel, "COM4");
      final CarpadControllerPortGui2 carPad = new CarpadControllerPortGui2("COM4", channel);
      //final CarpadControllerPortGui2 carPad = new CarpadControllerPortGui2("/dev/ttyS0", channel);

      // Executor
      ExecutorService signalExecutor = Executors.newSingleThreadExecutor();
      signalExecutor.execute(signalGen);

      final ExecutorService carPadExecutor = Executors.newSingleThreadExecutor();
      carPadExecutor.execute(carPad);

      // Try to connect if disconnected
      /*
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
       */

      /*
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
*/
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
      System.out.println("Found ports: "+CarpadControllerPortGui2.listSerialPorts());

      //String comPort = CarpadControllerPortGui2.findCarController();
      //System.out.println(CarpadControllerPortGui2.testPort("qq coisa"));

      System.out.println("Launching CarPad");
      BlockingQueue<Command> channel = new ArrayBlockingQueue<Command>(1);
      CarpadControllerPortGui2 carPad = new CarpadControllerPortGui2("COM4", channel);
      ExecutorService carPadExecutor = Executors.newSingleThreadExecutor();
      carPadExecutor.execute(carPad);

      carPadExecutor.shutdown();
      /*
      SerialPort serialPort = CarpadControllerPortGui2.connectSerial("COM3", "Testing Ports");

      if(serialPort != null) {
         System.out.println("Port could be opened!");
         serialPort.close();
      }

      SerialPort serialPort2 = CarpadControllerPortGui2.connectSerial("COM3", "Testing Ports");

      if(serialPort2 != null) {
         System.out.println("Port could be opened again!");
         serialPort2.close();
      }
       */

      
   }

    private static void testLibraryExists() {
        String os = System.getProperty("os.name");
        String lowerOs = os.toLowerCase();
        System.out.println("Running on '"+os+"'.");

        if(lowerOs.startsWith("windows")) {
            // Check for DLLs
            File rxtxSerial = new File("rxtxSerial.dll");
            if(!rxtxSerial.exists()) {
                System.out.println("Missing file: rxtxSerial.dll. Exiting...");
                System.exit(1);
            }
        } else {
            System.out.println("Operating System '"+os+"' not supported.");
            //System.exit(1);
        }
    }

   private static void testKeybController() {
      BlockingQueue<Command> inputChannel = new ArrayBlockingQueue<Command>(1);
      BlockingQueue<Command> outputChannel = new ArrayBlockingQueue<Command>(1);
      KeyboardController keyb = new KeyboardController(inputChannel, outputChannel, 40);

      ExecutorService keybExecutor = Executors.newSingleThreadExecutor();
      keybExecutor.execute(keyb);
      keybExecutor.shutdown();


   }

   private static void attachDetachKeyboard() {
      
      //String portComm = CarpadControllerPortGui2.findCarController();
      //System.out.println("A porta foi: "+portComm);
      //CarpadControllerPortGui2 carpad = new CarpadControllerPortGui2(null);


      long millisInPeriod = 40;

      Launcher launcher = new Launcher(millisInPeriod, "COM4");
      
      
      launcher.init();
      launcher.attachKeyboard();
      try {
         Thread.sleep(2000);
         //launcher.execute();
      } catch (InterruptedException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      
      launcher.detachKeyboard();
      System.out.println("Detach!");

      try {
         Thread.sleep(2000);
         //launcher.execute();
      } catch (InterruptedException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      
      launcher.attachKeyboard();
      System.out.println("Attach Again.");
      //launcher.execute();

       
       
   }

   private static void testGui3() {
      long millisInPeriod = 40;

      Launcher launcher = new Launcher(millisInPeriod, "COM4");

      ExecutorService keybExecutor = Executors.newSingleThreadExecutor();
      keybExecutor.execute(launcher);
      

      /*
      launcher.init();

      // From time to time, check if there is any problem
      while(true) {
         // Check if Carpad got disconnected
         
      }
       */
   }

   private static void testJavaDriver() {
      long millisInPeriod = 40;


      Main main = new Main();

      ExecutorService keybExecutor = Executors.newSingleThreadExecutor();
      keybExecutor.execute(main);
       
   }

   
   private static void testJavaDriverWithConfig() {
      String dataname = "datamap.txt";
      DataMap datamap = new DataMap(dataname);

      

      String filename = "properties.txt";
      Properties properties = new Properties();
      properties.setProperty("Option2", "hello2");

      

      FileOutputStream output;
      boolean fileWritten = true;
      try {
         output = new FileOutputStream(filename);
         properties.store(output, "Comments!");
         output.close();
      } catch (FileNotFoundException ex) {
         logger.warning("Could not create configuration file '" + filename + "'.");
         fileWritten = false;
      } catch (IOException ex) {
         logger.warning("IOException while writing the configuration file '" + filename + "'.");
         fileWritten = false;
      }

      if(fileWritten) {
         logger.info("Configuration file '"+filename+"' written.");
      }





      /*
      String filename = "config.dat";

      CarpadControllerData carpadData = openProtoFile(filename);

      System.out.println(carpadData.getKeyboardMaps().getTriggerDown());

      CarpadControllerData.newBuilder(carpadData)
              .setKeyboardMaps(CarpadControllerData.newBuilder()
              .);

      carpadData.toBuilder().setKeyboardMaps(carpadData.getKeyboardMaps().toBuilder().setTriggerDown(10));

      System.out.println(carpadData.getKeyboardMaps().getTriggerDown());

*/
//      writeProtoFile(carpadData, filename);

      /*
      CarpadController.Builder carpadBuilder = CarpadController.newBuilder();

      // Try to open the file
      FileInputStream streamFile = null;
      boolean fileExists = true;

      try {
         streamFile = new FileInputStream(filename);
      } catch (FileNotFoundException ex) {
         fileExists = false;
         logger.info("File '"+filename+"' not found. Creating new configuration " +
                 "file.");
      }

      // Merge contents of file into object
      if(fileExists) {
         try {
            carpadBuilder.mergeFrom(streamFile);
         } catch (IOException ex) {
            logger.warning("IOException while reading configuration file '"+filename+"'.");
         }
      } 
      
      // Build the DataFile
      CarpadController carpadData = carpadBuilder.build();
      
      // Create new file on disk
      if(!fileExists) {
         writeProtoFile(carpadData, filename);
      }
       */


   }

   private static CarpadControllerData openProtoFile(String filename) {
      CarpadControllerData.Builder carpadBuilder = CarpadControllerData.newBuilder();
      boolean fileExists = true;

       // Try to open the file
      FileInputStream streamFile = null;
      try {
         streamFile = new FileInputStream(filename);
      } catch (FileNotFoundException ex) {
         fileExists = false;
      }

      // Merge contents of file into object
      if(fileExists) {
         try {
            carpadBuilder.mergeFrom(streamFile);
         } catch (IOException ex) {
            logger.warning("IOException while reading configuration file '"+filename+"'.");
         }
      }

      // Build CarpadController
      CarpadControllerData carpadData = carpadBuilder.build();

      // Create new file on disk if it doesn't exist.
      if(!fileExists) {
                  logger.info("File '"+filename+"' not found. Creating new configuration " +
                 "file.");
         writeProtoFile(carpadData, filename);
      }

      return carpadData;
   }

   private static void writeProtoFile(CarpadControllerData carpadData, String filename) {
      FileOutputStream output;
      boolean fileWritten = true;
      try {
         output = new FileOutputStream(filename);
         carpadData.writeTo(output);
         output.close();
      } catch (FileNotFoundException ex) {
         logger.warning("Could not create configuration file '" + filename + "'.");
         fileWritten = false;
      } catch (IOException ex) {
         logger.warning("IOException while writing the configuration file '" + filename + "'.");
         fileWritten = false;
      }

      if(fileWritten) {
         logger.info("Configuration file '"+filename+"' written.");
      }
   }

   private static void testKeyMappings() {
      int key;
      key = KeyEvent.VK_L;
      System.out.println(KeyEvent.getKeyText(key)+":"+key);

      key = KeyEvent.VK_J;
      System.out.println(KeyEvent.getKeyText(key)+":"+key);

      key = KeyEvent.VK_A;
      System.out.println(KeyEvent.getKeyText(key)+":"+key);

      key = KeyEvent.VK_Z;
      System.out.println(KeyEvent.getKeyText(key)+":"+key);
   }

   private static void testPreferences() {
      //String preferencePackage = "com.pt.ualg.Car";
      //Class className = Class.forName(preferencePackage);
      
      //Preferences pref = Preferences.userNodeForPackage(PrefCarpad.classValue);
      Preferences pref = PrefCarpad.getPreferences();
      
      try {
         System.out.println(Arrays.toString(pref.keys()));
         // Store as String
         //PrefCarpad option = PrefCarpad.CalibrationTriggerNeutralInt;
         //pref.put(option.name(), option.defaultValue());
         // Retrive as Int
         //System.out.println(pref.getInt(option.name(), -1));
         //pref.putInt("INT", 21);
//pref.remove("INT");
//      System.out.println(pref.getInt("INT", 0));
         //System.out.println(pref.get("net", null));
      } catch (BackingStoreException ex) {
         Logger.getLogger(TestMain.class.getName()).log(Level.SEVERE, null, ex);
      }

      // Store as String
      //PrefCarpad option = PrefCarpad.CalibrationTriggerNeutralInt;
      //pref.put(option.name(), option.defaultValue());

      // Retrive as Int

      //System.out.println(pref.getInt(option.name(), -1));
      //pref.putInt("INT", 21);
//pref.remove("INT");
//      System.out.println(pref.getInt("INT", 0));
      //System.out.println(pref.get("net", null));
   }


   private static void testCortado() {
      JFrame frame = new JFrame();

      Cortado cortado = new Cortado();
cortado.setParam("url", "file://C:/Cortado/2008_Indy_500_video.ogv");
//cortado.setParam("autoPlay", "true");
cortado.setSize(352, 288);
cortado.setVisible(true);
      ExecutorService cortadoExec = Executors.newSingleThreadExecutor();
      
      cortadoExec.execute(cortado);
      //cortadoExec.shutdown();



/*
String[][] params = cortado.getParameterInfo();
      for(int i=0; i<params.length; i++) {
         //System.out.println(params[i][0]);
         System.out.println(i+"-"+"Param:"+params[i][0]+"; Value:"+cortado.getParam(params[i][0], "default"));
         //System.out.println();
      }
*/
/*
      BorderLayout border = new BorderLayout(10, 10);
      border.addLayoutComponent(cortado, BorderLayout.CENTER);


      frame.setLayout(border);
*/
/*
      frame.add(cortado);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
*/
      //cortado.setVisible(true);
   }



   private static Logger logger = Logger.getLogger(TestMain.class.getName());




}
