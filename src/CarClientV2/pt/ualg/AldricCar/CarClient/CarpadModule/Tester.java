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

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.amaze.ASL.LoggingUtils;
import pt.amaze.ASL.TimeUtils;
import pt.amaze.ASLCandidates.Concurrent.ReadChannel;
import pt.amaze.ASLCandidates.RxtxUtils;
import pt.amaze.ASLCandidates.Time.GlobalTimer;

/**
 *
 * @author Ancora Group <ancora.codigo@gmail.com>
 */
public class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       init();

        //testLoopWithoutExceptions();
        //testLoopWithException();
       //testCarpadUtils();
       testCarpadReader();
    }


   private static void init() {
      // Logging
       LoggingUtils.setupConsoleOnly();
       pt.amaze.ASLCandidates.LoggingUtils.redirectSystemOut();
       pt.amaze.ASLCandidates.LoggingUtils.redirectSystemErr();

       boolean librariesExist = RxtxUtils.rxtxLibrariesExists();

       if(!librariesExist) {
          Logger.getLogger(Tester.class.getName()).
                  warning("Libraries not found. Exiting...");
          System.exit(1);
       }
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

   private static void testCarpadUtils() {
      /**
       * Test finding carpad port
       */

      //String carpadPortName = CarpadUtils.findCarpadPortName();
      //System.out.println("CarpadPortName: "+carpadPortName);

      /**
       * Test detecting carpad
       */
      String carpadPortName = "COM4";
      boolean detected = CarpadUtils.testCarpadPort(carpadPortName);
      System.out.println(detected);
      
   }

   private static void testCarpadReader() {
      String portName = "COM4";
      //String portName = CarpadUtils.findCarpadPortName();
      
      if(portName == null) {
         System.out.println("Could not find port.");
         return;
      }

      CarpadReader carpadReader = new CarpadReader();
      ReadChannel<int[]> channel = carpadReader.getReadChannel();
      boolean activated = carpadReader.activate(portName);
      if(!activated) {
         System.out.println("Could not connect. Exiting...");
         System.exit(1);
      }
      //carpadReader.activate("COM4");
      
      ExecutorService exec = Executors.newSingleThreadExecutor();
      exec.submit(carpadReader);
      exec.shutdown();

      // Read values
      int counter = 0;
      while(counter < 100 && !exec.isTerminated()) {
         try {
            GlobalTimer.startTimer();
            // First read needs more time.
            int[] values = channel.poll(2000, TimeUnit.MILLISECONDS);
            GlobalTimer.stopTimer();
            System.out.println("Time in Millis:"+TimeUtils.nanosToMillis(GlobalTimer.getIimeIntervalNanos()));
            

            //System.out.println("Values:"+Arrays.toString(values));
         } catch (InterruptedException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
         }

         counter++;
      }

      System.out.println("Carpad Stopped. Trying to connect again after six seconds.");
      try {
         Thread.sleep(6000);
      } catch (InterruptedException ex) {
         Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
      }

      //if(!carpadReader.activate("COM4")) {
      if(!carpadReader.activate(portName)) {
         System.out.println("Second chance.");
         //carpadReader.activate("COM4");
         carpadReader.activate(portName);
      }
      exec = Executors.newSingleThreadExecutor();
      exec.submit(carpadReader);
      exec.shutdown();

      int[] values;
      try {
         values = channel.poll(2000, TimeUnit.MILLISECONDS);
            System.out.println("Values:"+Arrays.toString(values));
      } catch (InterruptedException ex) {
         Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
      }

      /*
      System.out.println("Going to shutdown forcibly.");


      exec.shutdownNow();
      try {
         exec.awaitTermination(1000, TimeUnit.MILLISECONDS);
      } catch (InterruptedException ex) {
         Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
         Thread.currentThread().interrupt();
      }
      */
      
   }


}
