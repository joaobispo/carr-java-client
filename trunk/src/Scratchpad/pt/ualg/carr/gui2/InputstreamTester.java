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

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ualg.carr.client1.Command;

/**
 *
 * @author Joao Bispo
 */
public class InputstreamTester implements Runnable {

   public InputstreamTester(InputStream inputStream) {
      this.inputStream = inputStream;
      this.result = false;
   }





   @Override
   public void run() {
      try {
// Inputstream can be read safely
         int readInt = inputStream.read();
         // REMOVE
         //System.out.println("Read int ("+readInt+")");
         boolean is255 = readInt == 255;
         //long initialNanos = System.nanoTime();
         while (!is255) {
            // Test if there is a timeout
            /*
            long elapsedTime = System.nanoTime() - initialNanos;
            if (elapsedTime > INPUTSTREAM_TIMEOUT_NANOS) {
            System.out.println("Timeout");
            return false;
            }
             */
            // Test if there are more bytes to read
            /*
            numReads = inputStream.available();
            if (numReads == 0) {
            return false;
            }
             */
            // Read again
            readInt = inputStream.read();
            is255 = readInt == 255;
         }
         // Check if port is outputing a 255
         // Add a new line.
         //System.out.println("");
         // It is outputing stuff besides '-1'. Check for the following pattern:
         // 255 [number] [number] ... 255
         // Search for a 255 inside an interval of numbers
         int period = Command.NUM_PORTS;
         int periodCounter = 0;
         while (readInt != 255) {
            readInt = inputStream.read();
            periodCounter++;
            // Check if it already passed the time a 255 should have appeard.
            if (periodCounter > period) {
               result = false;
            }
         }
         // Found a 255. Check if the next X numbers are not 255, and then appears
         // a 255 again
         for (int i = 0; i < period; i++) {
            readInt = inputStream.read();
            if (readInt == 255) {
               result = false;
            }
         }
         // Final check: if the next readInt is 255, we found a CarPad.
         readInt = inputStream.read();
         if (readInt == 255) {
            result = true;
         } else {
            result = false;
         }
      } catch (IOException ex) {
         Logger.getLogger(InputstreamTester.class.getName()).log(Level.SEVERE, null, ex);
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private InputStream inputStream;
   private boolean result;

}
