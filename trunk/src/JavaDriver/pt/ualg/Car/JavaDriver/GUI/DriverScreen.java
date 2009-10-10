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

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import pt.ualg.Car.JavaDriver.System.DriverInput;

/**
 *
 * @author Joao Bispo
 */
public class DriverScreen {

   public DriverScreen(List<GuiListener> listeners) {
      numInputs = DriverInput.numberOfInputs();
      this.listeners = listeners;
   }

   public JFrame getWindowFrame() {
      return windowFrame;
   }

   /**
    * Initializes the Swing Components
    */
   public void initComponents() {
      windowFrame = new JFrame();

      buildInputsComponents();
      buildConfigComponents();
      buildConnectComponents();

     
      JPanel configPanel = makePanelButtons();
      JPanel inputsAndConfig = makePanelButtonsAndInputs(configPanel);

      JPanel driverPanel = makePanelMain(inputsAndConfig);
      


      windowFrame.add(driverPanel);

      windowFrame.setTitle("Carpad Driver");
      windowFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      windowFrame.pack();
   }

   

   private JPanel makePanelButtons() {
      //... Create an independent GridLayout panel of buttons.
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, GAP, GAP));

        buttonPanel.add(calibrateButton);
        buttonPanel.add(configButton);
        buttonPanel.add(connectButton);


        return buttonPanel;
   }

   private JPanel makePanelButtonsAndInputs(JPanel configPanel) {
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

      content.add(new Gap(GAP)  , pos.nextRow()); // Add a gap below

      // Second line
      content.add(inputLabels[1], pos.nextRow());
      content.add(new Gap(GAP), pos.nextCol()); // 2nd col
      content.add(inputTextFields[1], pos.nextCol()); // 3rd col

      content.add(new Gap(2*GAP), pos.nextRow());
 
        return content;
   }

   private JPanel makePanelMain(JPanel inputsAndConfig) {
      JPanel driverPanel = new JPanel();
      driverPanel.setLayout(new BorderLayout());
      driverPanel.setBorder(BorderFactory.createEmptyBorder(3,3,3,3));
      driverPanel.add(inputsAndConfig, BorderLayout.CENTER);

      driverPanel.add(connectionStatus, BorderLayout.SOUTH);

      return driverPanel;
   }


   private void buildInputsComponents() {

      inputLabels = new JLabel[numInputs];
      inputTextFields = new JTextField[numInputs];

      DriverInput[] portNames = DriverInput.values();
      for (int i = 0; i < numInputs; i++) {

         // Create JLabels
         inputLabels[i] = new JLabel();
         // Setup JLabels
         String portName = portNames[i].getName();
         inputLabels[i].setText(portName+":");
         inputLabels[i].setHorizontalAlignment(JTextField.LEFT);

         // Create JTextFields
         inputTextFields[i] = new JTextField(5);
         // Setup JTextFields
         inputTextFields[i].setEditable(false);
         inputTextFields[i].setHorizontalAlignment(JTextField.LEFT);
      }
   }


   private void buildConfigComponents() {
                    
      calibrateButton = new JButton("Calibrate");
      calibrateButton.setEnabled(false);
      configButton = new JButton("Redefine Keys");
      //configButton.setEnabled(false);
      configButton.addActionListener(new java.awt.event.ActionListener() {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            configButtonAction(evt);
         }
      });
   }


   private void buildConnectComponents() {
      connectButton = new JButton();
      connectButton.setEnabled(false);
      connectButton.setText("Connect");
      connectButton.addActionListener(new java.awt.event.ActionListener() {
         @Override
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            connectButtonAction(evt);
         }

      });


     connectionStatus = new JTextField("Initializing...", 20);
     connectionStatus.setEditable(false);
     connectionStatus.setHorizontalAlignment(JTextField.CENTER);
     connectionStatus.setMinimumSize(new Dimension(100, 40));
   }

   public void updateConnectionMessage(String message) {
      connectionStatus.setText(message);
   }


   public void updateInputs(int wheelAngle, int triggerAngle) {
      inputTextFields[DriverInput.WHEEL.ordinal()].setText(String.valueOf(wheelAngle));
      inputTextFields[DriverInput.TRIGGER.ordinal()].setText(String.valueOf(triggerAngle));
   }

   /**
    * Connect Button was pressed.
    * 
    * @param evt
    */
   private void connectButtonAction(ActionEvent evt) {
            for(GuiListener listener : listeners) {
               listener.processMessage(GuiAction.CONNECT_BUTTON);
            }
   }

   /**
    * Config Button was pressed.
    *
    * @param evt
    */
   private void configButtonAction(ActionEvent evt) {
      // Open RedefineKeys Screen
      redefineKeysDialog = new JDialog(windowFrame);
      redefineKeysDialog.setModalityType(ModalityType.APPLICATION_MODAL);
      redefineKeysDialog.setVisible(true);

   }

   public void activateConnectButton(boolean b) {
      connectButton.setEnabled(b);
   }

   /**
    * If true, ConnectButton says Connect. If false, text is set to Disconnect.
    * @param b
    */
   public void setConnectButtonText(String text) {
      connectButton.setText(text);
   }

   /*
   public void addListener(GuiListener guiListener) {
      listeners.add(guiListener);
   }
    */

   /**
    * INSTANCE VARIABLES
    */
   // Additional Windows
   private JDialog redefineKeysDialog;


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

   private List<GuiListener> listeners;

   // Constants
   private static final int BORDER = 12;  // Window border in pixels.
   private static final int GAP = 5;   // Default gap btwn components.


   }








