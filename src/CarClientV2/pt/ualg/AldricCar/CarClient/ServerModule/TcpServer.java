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

package pt.ualg.AldricCar.CarClient.ServerModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Time;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.amaze.ASL.TimeUtils;
import pt.amaze.ASLCandidates.ParseUtils;
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.Command;

/**
 *
 * @author Joao Bispo
 */
public class TcpServer implements Runnable {

   public TcpServer() {
      PreferencesEnum preferences = ServerPreferences.getPreferences();

      String serverPortString = preferences.getPreference(ServerPreferences.ServerPort);
      int serverPort = ParseUtils.parseInt(serverPortString);

      String intervalMillisString = preferences.getPreference(ServerPreferences.InfoIntervalMillis);
      long intervalMillis = ParseUtils.parseLong(intervalMillisString);
      infoIntervalNanos = TimeUtils.millisToNanos(intervalMillis);


         try {
            serverSocket = new ServerSocket(serverPort);
         } catch (IOException ex) {
            Logger.getLogger(TcpServer.class.getName()).
                 warning("IOException while trying to create TCP socket: "+ex.getMessage());
         serverSocket = null;
         }
      

      lastCounter = 0;
      firstPacket = true;
   }



   public void run() {
      byte[] commandBytes = new byte[Command.VARIABLE_ORDER.length];
      long packetCounter = 0;
      long startTime = System.nanoTime();

      // Make Connection
      System.out.println("Waiting for connection...");
      Socket socket = null;
      try {
         socket = serverSocket.accept();
      } catch (IOException ex) {
         Logger.getLogger(TcpServer.class.getName()).
                 warning("IOException while accepting connection to TCP socket: "+ex.getMessage());
         return;
      }
      System.out.println("Connection Accepted.");

      InputStream input = null;
      try {
         input = socket.getInputStream();
      } catch (IOException ex) {
         Logger.getLogger(TcpServer.class.getName()).
                 warning("IOException while retriving the InputStream: " + ex.getMessage());
         return;
      }


      while (true) {
         try {
            int readValue = input.read();
            while (readValue != -1) {
               System.out.print(readValue);
            }
            System.out.println("\nEnded Stream.");
         } catch (IOException ex) {
            Logger.getLogger(TcpServer.class.getName()).
                    warning("IOException while reading the InputStream: " + ex.getMessage());
            return;
         }
      }

      //while (true) {

         // receive request
         //DatagramPacket packet = new DatagramPacket(commandBytes, commandBytes.length);
        // try {

            //boolean acceptPacket = true;
            //serverSocket.receive(packet);

            // Check if packet arrived in order
            /*
            if(firstPacket) {
               lastCounter = commandBytes[0];
            } else {
               // Check if current counter - last counter is positive
               int diference = commandBytes[0] - lastCounter;

               if(diference < 0) {
                  System.out.println("Packet arrived out of order. This packet counter: "
                          +commandBytes[0]+". Last packet counter: "+lastCounter);
                  acceptPacket = false;
               }

               lastCounter = commandBytes[0];
            }
            */
            // Accept packet
            /*
            if(acceptPacket) {
               packetCounter++;
            }
             */

            // Check if it is time to show the packet count
            /*
            long nanoNow = System.nanoTime();
            long nanoDiff = nanoNow - startTime;
            if(nanoDiff > infoIntervalNanos) {
               System.out.println("Packet Counter:"+packetCounter+". Last received" +
                       " packet:"+ Arrays.toString(commandBytes));
               startTime = nanoNow;
            }
            */
         /*
         } catch (IOException ex) {
            Logger.getLogger(TcpServer.class.getName()).
                    warning("IOException in socket while waiting for packet: "+ex.getMessage());
         }
          */


      //}
   }

   /**
    * INSTANCE VARIABLES
    */
   private ServerSocket serverSocket;
   private byte lastCounter;
   private boolean firstPacket;
   private long infoIntervalNanos;
}
