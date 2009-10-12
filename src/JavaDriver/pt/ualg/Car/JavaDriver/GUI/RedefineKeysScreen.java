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

package pt.ualg.Car.JavaDriver.GUI;

import java.awt.Dialog.ModalityType;
import java.awt.Frame;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTextField;

/**
 *
 * @author Joao Bispo
 */
public class RedefineKeysScreen {

   public RedefineKeysScreen(Frame owner, List<GuiListener> listeners) {
      this.listeners = listeners;
      this.owner = owner;

      // Assume this will be running in the AWT Thread
      initComponents();
   }

   /**
    * Initializes the Swing Components
    */
   public void initComponents() {
      redefineKeysScreen = new JDialog(owner);

      // Build Screen Components
      buildComponents();
   }

   /**
    * Build the screen components
    */
   private void buildComponents() {
      int numInputs = inputs.length;

      // Buttons
      btAssignKeys = new JButton[numInputs];
      for(int i=0; i<inputs.length; i++) {
        btAssignKeys[0] = new JButton(inputs[i]);
      }

   }

   /**
    * Makes the screen appear. Modality is set to this screen, control is resumed
    * after window closes.
    */
   public void appear() {
      redefineKeysScreen.setModalityType(ModalityType.APPLICATION_MODAL);
      redefineKeysScreen.setVisible(true);
   }

   /**
    * INSTANCE VARIABLES
    */
   // This screen
   private JDialog redefineKeysScreen;

   // References to the outside worlds
   private Frame owner;
   private List<GuiListener> listeners;

   // Screen components
   private JTextField[] tfAssignKeys;
   private JButton[] btAssignKeys;

   private static final String[] inputs = {
   "Wheel Forward",
   "Wheel Backward",
   "Trigger Action",
   "Trigger Reverse"};

}
