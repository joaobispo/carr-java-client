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

package pt.ualg.carr.gui2;

import java.awt.EventQueue;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * Main screen of Car Client
 *
 * @author Joao Bispo
 */
public class MainScreen {

   public MainScreen(int numInputs) {
      this.numInputs = numInputs;

      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            initComponents();
         }
      });

   }

   public void setVisible() {
      EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            windowFrame.setVisible(true);
         }

      });
   }

   private void initComponents() {
      windowFrame = new JFrame();
      jLabels = new JLabel[numInputs];
      jTextFields = new JTextField[numInputs];


      JPanel inputsPanel = new JPanel();
      inputsPanel.setLayout(new GridLayout(numInputs, 2));
      
      for(int i=0; i<numInputs; i++) {
         // Create JTextFields
         jTextFields[i] = new JTextField();
         // Setup JTextFields
         jTextFields[i].setEditable(false);
         inputsPanel.add(jTextFields[i]);

         // Create JLabels
         jLabels[i] = new JLabel();
         // Setup JLabels
         jLabels[i].setText(" Port "+(i+1));
         inputsPanel.add(jLabels[i]);

      }


      // Setup frame
      windowFrame.add(inputsPanel);
      windowFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      windowFrame.pack();

   }

   /*
   private void initComponents() {
      windowFrame = new JFrame();
      jLabels = new JLabel[numInputs];
      jTextFields = new JTextField[numInputs];

      Container contentPane = windowFrame.getContentPane();
      GroupLayout layout = new GroupLayout(contentPane);

      contentPane.setLayout(layout);
      
      ParallelGroup parallelGroup = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
      //SequentialGroup sequentialGroup = layout.createSequentialGroup();
      //ParallelGroup jTextFieldGroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false);

      //parallelGroup.addGroup(layout.createSequentialGroup());
      //sequentialGroup.addContainerGap();
      //sequentialGroup.addGroup(jTextFieldGroup);

      for(int i=0; i<numInputs; i++) {
         // Create JTextFields
         jTextFields[i] = new JTextField();
         // Setup JTextFields
         jTextFields[i].setEditable(false);
         parallelGroup.addComponent(jTextFields[i], GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE);
         //jTextFieldGroup.addComponent(jTextFields[i], javax.swing.GroupLayout.Alignment.TRAILING);
         //contentPane.add(jTextFields[i]);

         // Create JLabels
         jLabels[i] = new JLabel();
         // Setup JLabels
         jLabels[i].setText("Port "+(i+1));
         //contentPane.add(jLabels[i]);
         //parallelGroup.addComponent(jLabels[i], javax.swing.GroupLayout.Alignment.TRAILING);


         // Setup disposition
      }

      layout.setHorizontalGroup(parallelGroup);
      layout.setVerticalGroup();
      
      
      // Setup frame
      windowFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      windowFrame.pack();

      javax.swing.
   }
    */

   /*
   public static MainScreen createScreen() {
      MainScreenLauncher launcher = new MainScreenLauncher();
      EventQueue.invokeAndWait(launcher);
      return launcher.getMainScreen();
   }
    */

   /**
    * INSTANCE VARIABLES
    */
   private JFrame windowFrame;
   private JLabel[] jLabels;
   private JTextField[] jTextFields;
   private int numInputs;


   /*
   class MainScreenLauncher implements Runnable {
      @Override
      public void run() {
         mainScreen = new MainScreen();
      }

      public MainScreen getMainScreen() {
         return mainScreen;
      }

      private MainScreen mainScreen;
   }
    */
}
