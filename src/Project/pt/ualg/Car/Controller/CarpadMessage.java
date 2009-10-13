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

package pt.ualg.Car.Controller;

import java.util.Arrays;

/**
 * Contains all the information for giving a command to the Car. This includes:
 * <p>- 6 angles that go from 0 to 179.
 * <p>- A counter.
 *
 * @author Joao Bispo
 */
public class CarpadMessage {

   /**
    * Creates a CarpadMessage object.
    *
    * @param counter
    * @param angles
    */
   public CarpadMessage(int[] angles) {
      this.counter = newCounterValue();
      this.angles = Arrays.copyOf(angles, NUM_PORTS);
   }

   public int[] getAngles() {
      return angles;
   }

   public int getCounter() {
      return counter;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder(100);
      for(int i=0; i<NUM_PORTS; i++) {
         builder.append("Port ");
         builder.append((i+1));
         builder.append(":");
         builder.append(angles[i]);
         builder.append("\n");
      }

      return builder.toString();
   }

   /**
    * Resets the global counter of commands to 0.
    */
   public void resetGlobalCounter() {
      globalCounter = 0;
   }

   /**
    *
    * @return creates a new counter value.
    */
   private int newCounterValue() {
      final int counterValue = globalCounter;
      globalCounter++;
      return counterValue;
   }

   /**
    * INSTANCE VARIABLES
    */
   private int counter;
   private final int[] angles;
   public final static int NUM_PORTS = CarpadInput.values().length;
   
   private static int globalCounter = 0;


}
