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

package pt.ualg.carr;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joao Bispo
 */
public class PadInputs {

   public PadInputs(InputStream input) {
      portValues = new int[NUM_PORTS];
      this.input = input;
   }

   public void update() {
      int readChar = 0;
      while(readChar != 255) {
         try {
            readChar = input.read();
         } catch (IOException ex) {
            Logger.getLogger(PadInputs.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

      // Read a 255! Now read the next 6 values
      for(int i=0; i<NUM_PORTS; i++) {
         try {
            portValues[i] = input.read();
         } catch (IOException ex) {
            Logger.getLogger(PadInputs.class.getName()).log(Level.SEVERE, null, ex);
         }
      }

   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(100);
      for(int i=0; i<NUM_PORTS; i++) {
         builder.append("Port ");
         builder.append((i+1));
         builder.append(":");
         builder.append(portValues[i]);
         builder.append("\n");
      }

      return builder.toString();
   }



   /**
    * INSTANCE VARIABLES
    */
   private int[] portValues;
   InputStream input;

   private static final int NUM_PORTS = 6;
}
