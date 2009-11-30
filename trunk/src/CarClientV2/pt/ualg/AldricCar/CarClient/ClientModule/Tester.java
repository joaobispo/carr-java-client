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

package pt.ualg.AldricCar.CarClient.ClientModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.prefs.Preferences;
import pt.amaze.ASLCandidates.Identification.ByteIdentifier;
import pt.amaze.ASLCandidates.Preferences.PropertiesDefinition.Section;
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;
import pt.amaze.ASLCandidates.PreferencesUtil;
import pt.ualg.AldricCar.CarClient.CarpadModule.CarpadUtils;
import pt.ualg.AldricCar.CarClient.CommandOutput;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.Command;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.CommandSource;
import pt.ualg.AldricCar.CarClient.Main;
import pt.ualg.AldricCar.CarClient.ServerModule.ServerExperience;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class Tester {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      Main.init();

      //testPreferences();
      //testCarpadCommandSource(args);
      //testPreferencesAndProperties();
      testClientCore();
   }

   private static void testPreferences() {
      PreferencesEnum preferences = ClientPreferences.getPreferences();
      
      // Get value that was in properties file
      String value = preferences.getPreferenceReal(ClientPreferences.ServerAddress);
      System.out.println("Value of ServerAddress:"+value);
   }

   private static void testCarpadCommandSource(String args[]) {
      ByteIdentifier byteId = new ByteIdentifier();
      long timeout = 500;
      CommandSource csource = new CarpadCommandSource(byteId, timeout);

      int numberOfTestReads = 10;

      if (args.length != 0) {
         try {
            int i = Integer.parseInt(args[0]);
            numberOfTestReads = i;
         } catch (NumberFormatException ex) {
         }
      }

      // Is Connected?
      //System.out.println("Is Connected?:"+csource.isConnected());

      // Try to connect
      System.out.println("Connecting... :");
      boolean connected = csource.connect();
      //System.out.println("Connecting... :"+connected);
      

      if(!connected) {
         System.out.println("Could not connect.");
         System.exit(1);
      } else {
         System.out.println("Connected.");
      }

      // Read some values
      /*
      int numValues = 40;
      for(int i=0; i<numValues; i++) {
         System.out.println(csource.readCommand());
      }
       */
       System.out.println("Began test.");
      testDroppedPackets(csource, numberOfTestReads);

      // Disconnect
      csource.disconnect();
      System.out.println("Disconnected.");

      // Connect again
      System.out.println("Connecting 2n time...");
      connected = csource.connect();
      if(!connected) {
         System.out.println("Could not connect the 2nd time.");
         System.exit(1);
      }

      System.out.println("Connected 2n time.");

      System.out.println("Test 2nd time.");
      testDroppedPackets(csource, numberOfTestReads);

      csource.disconnect();
      System.out.println("Disconnected 2nd time.");
   }

   /**
    * Given a running csource, tests for dropping packets
    * @param csource
    */
   private static void testDroppedPackets(CommandSource csource, int numberOfReads) {
      int testSize = numberOfReads;

      Command command = null;
      int counter = 0;
      while(command == null) {
         command = csource.readCommand();
         counter++;
      }
      if(counter > 1) {
         System.out.println("Read '"+counter+"' null commands before starting test.");
      }

      byte lastReadValue = (byte) command.getValue(Command.Variable.COUNTER);
      for(int i=0; i<testSize; i++) {
         if(i%50 == 0) {
            System.out.println("Iteration "+i);
         }
         // Read next command
         command = csource.readCommand();
         if(command == null) {
            System.out.println("Command == null at iteration "+i+". Exiting...");
            System.exit(1);
         }

         byte newByte = (byte) command.getValue(Command.Variable.COUNTER);

         // Is only 1 command appart?
         int distance = newByte - lastReadValue;
         if(distance > 1) {
            System.out.println("Dropped ["+(distance-1)+"] Commands at iteration "+i+".");
         }

         lastReadValue = newByte;

         //System.out.println(command);
      }

      System.out.println("Test Ended");
   }

   private static void testPreferencesAndProperties() {
      PreferencesEnum prefEnum = ClientPreferences.getPreferences();

      //PreferencesEnum prefEnum = new PreferencesEnum(ClientPreferences.class, true, clientProp);

      //System.out.println("Contains 'string':"+clientProp.valueOf("String"));
      //System.out.println("Contains enum:"+clientProp.valueOf(ClientPreferences.FirstReadTimeoutMillis.getKey()));

      //PreferencesUtil.savePropertiesDefinition(clientProp, preferences);

      //System.out.print(PreferencesUtil.generateProperties(prefEnum));
      //prefEnum.putPreference(ClientPreferences.SerialPortName, "ttyUSB0");
      //System.out.println(prefEnum.getPreference(ClientPreferences.SerialPortName));
   }

   private static void testClientCore() {
      ClientCore clientCore = new ClientCore();

      ClientListener commandOutput = new CommandOutput();
      ClientListener clientSocket = new ClientSocket();

      //clientCore.addListener(commandOutput);
      clientCore.addListener(clientSocket);


      // Create server and start server
      //createAndStartServer();


      clientCore.run();
   }

   private static void createAndStartServer() {
      ServerExperience server = new ServerExperience();
      ExecutorService executor = Executors.newSingleThreadExecutor();
      executor.submit(server);
      executor.shutdown();
   }


}
