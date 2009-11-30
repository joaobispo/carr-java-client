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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.amaze.ASLCandidates.ParseUtils;
import pt.amaze.ASLCandidates.Preferences.PreferencesEnum;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.Command;

/**
 * Sends Commands to the Server over internet.
 *
 * @author Joao Bispo
 */
public class ClientSocket implements ClientListener {

   public ClientSocket() {
      PreferencesEnum preferences = ClientPreferences.getPreferences();

      // Get address and port from Preferences
      String portString = preferences.getPreference(ClientPreferences.ServerPort);
      serverPort = ParseUtils.parseInt(portString);

      String serverAddressName = preferences.getPreference(ClientPreferences.ServerAddress);
      setServerAddress(serverAddressName);

      try {
         socket = new DatagramSocket();
      } catch (SocketException ex) {
         Logger.getLogger(ClientSocket.class.getName()).
                 warning("Couldn't connect Datagram Socket: "+ex.getMessage());
      }

   }

   /**
    * Attempts to set the address of the server.
    *
    * @param addressName
    * @return true if there were no exceptions.
    */
   public boolean setServerAddress(String addressName) {

      try {
         serverAddress = InetAddress.getByName(addressName);
      } catch (UnknownHostException ex) {
         Logger.getLogger(ClientSocket.class.getName()).
                 warning("Couldn't resolve server address: '"+addressName+"'");


         Logger.getLogger(ClientSocket.class.getName()).
                 warning("Exiting...");
         System.exit(1);

         //serverAddress = null;
         //return false;
      }

      return true;
   }

   public void newCommand(Command command) {
      // Check if socket is connected
      if(serverAddress == null) {
         Logger.getLogger(ClientSocket.class.getName()).
                 warning("Can't send Command, server address is not resolved.");
         return;
      }

      byte[] byteArray = command.getByteArray();
      DatagramPacket packet = new DatagramPacket(byteArray, byteArray.length,
              serverAddress, serverPort);
      try {
         socket.send(packet);
      } catch (IOException ex) {
         Logger.getLogger(ClientSocket.class.getName()).
                 warning("IOException while trying to send Command over UDP:" +
                 ex.getMessage());
      }
/*
      DatagramPacket packet = new DatagramPacket(buf, buf.length,
                                           address, 4445);
socket.send(packet);
*/
   }

   /**
    * INSTANCE VARIABLES
    */
   private int serverPort;
   private InetAddress serverAddress;
   private DatagramSocket socket;


}
