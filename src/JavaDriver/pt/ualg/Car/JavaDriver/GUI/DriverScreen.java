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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
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

      JPanel configPanel = buildConfigPanel();

      buildJFrameBagLayout(configPanel);
      //buildJFrameGridLayout(inputsPanel, connectPanel);

      windowFrame.setTitle("Carpad Driver");
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

         // Create JLabels
         inputLabels[i] = new JLabel();
         // Setup JLabels
         String portName = portNames[i].getName();
         inputLabels[i].setText(portName+":");
         inputLabels[i].setHorizontalAlignment(JTextField.LEFT);
         inputsPanel.add(inputLabels[i]);

         // Create JTextFields
         inputTextFields[i] = new JTextField(5);
         // Setup JTextFields
         inputTextFields[i].setEditable(false);
         inputTextFields[i].setHorizontalAlignment(JTextField.LEFT);
         inputsPanel.add(inputTextFields[i]);

      }

      return inputsPanel;
   }


   private JPanel buildConnectPanel() {
     JPanel connectPanel = new JPanel();
     GridLayout gridLayout = new GridLayout(2, 1);
     gridLayout.setVgap(10);
     connectPanel.setLayout(gridLayout);

     connectButton = new JButton("Connect");
     connectButton.setEnabled(false);
     connectPanel.add(connectButton);

     connectionStatus = new JTextField("Initializing...", 20);
     connectionStatus.setEditable(false);
     connectionStatus.setHorizontalAlignment(JTextField.CENTER);
     connectionStatus.setMinimumSize(new Dimension(100, 40));
     connectPanel.add(connectionStatus);

     return connectPanel;
   }

   private JPanel buildConfigPanel() {
      //... Create an independent GridLayout panel of buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, GAP, GAP));

        calibrateButton = new JButton("Calibrate");
        buttonPanel.add(calibrateButton);
        configButton = new JButton("Redefine Keys");
        buttonPanel.add(configButton);


        //connectButton = new JButton("Connect");
        //connectButton.setEnabled(false);
        //buttonPanel.add(connectButton);

        return buttonPanel;
   }

   private JPanel buildJFrameBagLayout(JPanel configPanel) {
      //... Create GridBagLayout content pane; set border.
      JPanel content = new JPanel(new GridBagLayout());
      content.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER,
              BORDER, BORDER));

      GridBagHelper pos = new GridBagHelper();  // Create GridBag helper object.

      // First Line
      content.add(inputLabels[0], pos);
      content.add(new Gap(GAP), pos.nextCol()); // 2nd col
      content.add(inputTextFields[0], pos.nextCol()); // 3rd col

      content.add(new Gap(GAP), pos.nextCol()); // 4th col
      content.add(configPanel, pos.nextCol().height(5).align(GridBagConstraints.NORTH)); // 5th col

      //content.add(new Gap(GAP) , pos.nextRow());  // Add a gap below
      content.add(new Gap(GAP)  , pos.nextRow());
      // Second line
      content.add(inputLabels[1], pos.nextRow());
      content.add(new Gap(GAP), pos.nextCol()); // 2nd col
      content.add(inputTextFields[1], pos.nextCol()); // 3rd col

      

      /*
      for (int i = 0; i < numInputs; i++) {
         content.add(inputLabels[i], pos);
         content.add(new Gap(GAP), pos.nextCol());
         //content.add(inputTextFields[i], pos.nextCol().expandW());
         content.add(inputTextFields[i], pos.nextCol());

         if (i == 0) {
            content.add(new Gap(GAP), pos.nextCol());
            content.add(configPanel, pos.nextCol().height(5).align(GridBagConstraints.NORTH));
         }

         content.add(new Gap(GAP), pos.nextRow());  // Add a gap below
         pos.nextRow();
      }
      */

      //content.add(new Gap(GAP), pos.nextRow());  // Add a gap below

      
      //... Last content row.
//content.add(new Gap()  , pos.nextRow().width().expandH());
      //content.add(connectionStatus, pos.nextCol().nextCol());
      //content.add(connectionStatus, pos.nextRow().nextCol().nextCol());
         //... Add an area that can expand at the bottom.

        //content.add(new Gap()  , pos.nextRow().width().expandH());



        
        windowFrame.add(content);
        return content;
   }


   private void buildJFrameGridLayout(JPanel inputsPanel, JPanel connectPanel) {

      // Setup Frame
      GridLayout gridLayout = new GridLayout(2,1);
      gridLayout.setVgap(20);

      windowFrame.setLayout(gridLayout);
      windowFrame.add(inputsPanel);
      windowFrame.add(connectPanel);
   }


   public void updateConnectionMessage(String message) {
      connectionStatus.setText(message);
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

   private JButton calibrateButton;
   private JButton configButton;

   private int numInputs;

   // Constants
   private static final int BORDER = 12;  // Window border in pixels.
   private static final int GAP = 5;   // Default gap btwn components.





}
