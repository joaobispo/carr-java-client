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

package pt.ualg.AldricCar.CarClient.CarpadModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        testLoopWithoutExceptions();
        testLoopWithException();
    }

    
   private static void testLoopWithoutExceptions() {
      Runnable runnable = new Runnable() {

         public void run() {
            long counter = 0;
            final long divisible = 1000000;
            boolean running = true;

            while(running) {
               if(counter % divisible == 0) {
                  System.out.println("testLoopWithoutExceptions");
               }
               counter ++;

               // Check if thread was interrupted
               if(running) {
                  if(Thread.currentThread().isInterrupted()) {
                     running = false;
                  }
               }
            }
         }

      };

      ExecutorService carpadExec = Executors.newSingleThreadExecutor();
      carpadExec.submit(runnable);

      
      try {
         //Sleep a little
         Thread.sleep(2000);
      } catch (InterruptedException ex) {
         Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
         Thread.currentThread().interrupt();
      }
       

      carpadExec.shutdownNow();

   }

   // Needs serialport and inputstream to be tested
   private static void testLoopWithException() {
      Runnable runnable = new Runnable() {

         public void run() {
            long counter = 0;
            final long divisible = 1000000;
            boolean running = true;

            while(running) {

               if(counter % divisible == 0) {
                  System.out.println("testLoopWithoutExceptions");
               }
               counter ++;

               // Check if thread was interrupted
               if(running) {
                  if(Thread.currentThread().isInterrupted()) {
                     running = false;
                  }
               }
            }
         }

      };

      ExecutorService carpadExec = Executors.newSingleThreadExecutor();
      carpadExec.submit(runnable);


      try {
         //Sleep a little
         Thread.sleep(2000);
      } catch (InterruptedException ex) {
         Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
         Thread.currentThread().interrupt();
      }


      carpadExec.shutdownNow();
   }

}
