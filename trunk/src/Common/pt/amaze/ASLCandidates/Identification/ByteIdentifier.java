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

package pt.amaze.ASLCandidates.Identification;

/**
 * Generates integers, incrementally, which can be used to uniquely identify
 * objects.
 * 
 * @author Joao Bispo
 */
public class ByteIdentifier {

   /**
    * Creates a IntIdentifier that will generate integers starting from 0.
    */
   public ByteIdentifier() {
      this((byte)0);
   }

   
   /**
    * Creates a IntIdentifier that will generate integers starting from the 
    * given value, inclusive.
    * 
    * @param startValue
    */
   public ByteIdentifier(byte startValue) {
      this.currentValue = startValue;
   }


   /**
    * Returns a new integer.
    *
    * @return
    */
   public int newByte() {
      final byte returnByte = currentValue;
      currentValue++;

      return returnByte;
   }

   /**
    * INSTANCE VARIABLES
    */
   private byte currentValue;
}
