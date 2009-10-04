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

package pt.ualg.carr.gui3;

import java.awt.GridLayout;
import java.util.Arrays;
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

   }

   public JFrame getWindowFrame() {
      return windowFrame;
   }
  

   /**
    * Initializes the Swing Components
    */
   public void initComponents() {
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

   /**
    * Updates the values that appear on the textfields.
    * 
    * @param command
    */
   public void updateTextFields(int[] values) {
      if(values.length < numInputs) {
         values = Arrays.copyOf(values, numInputs);
      }

      for(int i=0; i<numInputs; i++) {
               jTextFields[i].setText(String.valueOf(values[i]));
            }
   }


   /**
    * INSTANCE VARIABLES
    */
   private JFrame windowFrame;
   private JLabel[] jLabels;
   private JTextField[] jTextFields;
   private int numInputs;

}
