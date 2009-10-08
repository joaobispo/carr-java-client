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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import pt.ualg.Car.JavaDriver.System.JavaDriverInput;

/**
 *
 * @author Joao Bispo
 */
public class DriverScreen {

   public DriverScreen() {
      numInputs = JavaDriverInput.numberOfInputs();
   }

   public JFrame getWindowFrame() {
      return windowFrame;
   }

   /**
    * Initializes the Swing Components
    */
   public void initComponents() {
      windowFrame = new JFrame();

      // Build Inputs Panel
      JPanel inputsPanel = buildInputsPanel();

      JPanel connectPanel = buildConnectPanel();

      // Setup Frame
      GridBagLayout gridBag = new GridBagLayout();
      GridBagConstraints bagCons = new GridBagConstraints();

      //Insets insets = new Insets(20, 20, 20, 20);
      //bagCons.insets = insets;
      bagCons.weightx = 10.0;

      gridBag.setConstraints(windowFrame, bagCons);
      
      windowFrame.setLayout(gridBag);
      windowFrame.add(inputsPanel);
      windowFrame.add(connectPanel);
      
      windowFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      windowFrame.pack();
   }

   private JPanel buildInputsPanel() {
      JPanel inputsPanel = new JPanel();
      inputsPanel.setLayout(new GridLayout(numInputs, 2));


      inputLabels = new JLabel[numInputs];
      inputTextFields = new JTextField[numInputs];

      JavaDriverInput[] portNames = JavaDriverInput.values();
      for (int i = 0; i < numInputs; i++) {
         // Create JTextFields
         inputTextFields[i] = new JTextField();
         // Setup JTextFields
         inputTextFields[i].setEditable(false);
         inputsPanel.add(inputTextFields[i]);

         // Create JLabels
         inputLabels[i] = new JLabel();
         // Setup JLabels
         String portName = portNames[i].getName();
         inputLabels[i].setText(" " + portName);
         inputsPanel.add(inputLabels[i]);


      }

      return inputsPanel;
   }


   private JPanel buildConnectPanel() {
     JPanel connectPanel = new JPanel();
     connectPanel.setLayout(new GridLayout(2, 1));

     connectButton = new JButton("Connect");
     connectButton.setEnabled(false);
     connectPanel.add(connectButton);

     connectionStatus = new JTextField("Initializing...");
     connectionStatus.setEditable(false);
     connectionStatus.setHorizontalAlignment(JTextField.CENTER);
     connectPanel.add(connectionStatus);

     return connectPanel;
   }


   /**
    * INSTANCE VARIABLES
    */
   // Panel Inputs Info
   private JFrame windowFrame;
   private JLabel[] inputLabels;
   private JTextField[] inputTextFields;
   // Panel Connection Status and Button
   private JTextField connectionStatus;
   private JButton connectButton;

   private int numInputs;


}
