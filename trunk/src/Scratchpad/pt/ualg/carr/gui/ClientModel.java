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

package pt.ualg.carr.gui;

import javax.swing.JFrame;
import pt.ualg.carr.client1.Command;
import pt.ualg.carr.client1.CommandListener;

/**
 * Contains the state of the client. It includes the last input sent to the car
 * and the stream of video received from the car.
 *
 * <p> Any changes in the model are invoked through the AWT Event Queue.
 *
 * @author Joao Bispo
 */
public class ClientModel implements CommandListener {

   public ClientModel() {
      lastCommand = new Command(-1, new int[Command.NUM_PORTS]);
      mainWindow = GuiLauncher.startMainWindow();
   }



   /**
    * Inform the GUI
    * @param command
    */
   @Override
   public void processCommand(final Command command) {
      // Update the model
      java.awt.EventQueue.invokeLater(new Runnable() {

         @Override
         public void run() {
            lastCommand = command;
         }
      });
      
      mainWindow.processCommand(command);
   }

   /**
    * INSTANCE VARIABLES
    */
   private Command lastCommand;
   // Something for the video
   // Handler for the GUI's main window
   private MainWindow mainWindow;
}
