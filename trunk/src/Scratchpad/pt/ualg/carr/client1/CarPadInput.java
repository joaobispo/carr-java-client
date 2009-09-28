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

package pt.ualg.carr.client1;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads a stream of commands from the Car input and
 * puts them in a BlockingQueue.
 *
 * @author Joao Bispo
 */
public class CarPadInput implements Runnable {

   public CarPadInput(BlockingQueue<Command> channel, String commPortName) {
      this.portName = commPortName;
      this.channel = channel;
      this.inputPort =  null;
      //this.inputPort =  startPort(commPortName);
      this.run = true;
      this.inputStream = null;
      this.counter = 0;
   }

   public SerialPort startPort(String commPortName) {
      CommPortIdentifier commPortId = null;
      try {
         commPortId = CommPortIdentifier.getPortIdentifier(commPortName);
      } catch (NoSuchPortException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

      SerialPort port = null;
      try {
         port = (SerialPort) commPortId.open("Car Command", 4000);
      } catch (PortInUseException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

      return port;
   }

   @Override
   public void run() {
      initializeInputStream();

      /*
      try {
         inputStream = inputPort.getInputStream();
      } catch (IOException ex) {
         logger.severe("Could not open InputStream for Car Input.");
      }

     
      // If InputStream could not be opened, shutdown.
      if(inputStream == null) {
         shutdown();
      }
      */

      // Read input stream as fast as it can
      while(run) {
         int readInt = INPUT_STREAM_READ_ERROR;
         try {
            readInt = inputStream.read();
         } catch (IOException ex) {
            logger.severe("IO Exception while trying to read inputStream of CarPad.");
            inputPort.close();
            initializeInputStream();
         }

         //System.out.println("ReadInt:"+readInt);

         // If couldn't read input stream, terminate
         if(readInt == INPUT_STREAM_READ_ERROR) {
            //restart();
            //shutdown();
            inputPort.close();
         }


         // If read number is commandStart, process package and put it in the queue.
         if(readInt == COMMAND_START) {
            processCommand();
         }
         
      }
   }

   /**
    * Creates a Command object and puts it in the queue, if the queue is empty.
    */
   private void processCommand() {
      // Create array for inputs
      int[] angles = new int[Command.NUM_PORTS];

      // Read as many commands as necessary
      for(int i=0; i<Command.NUM_PORTS; i++) {
         try {
            angles[i] = inputStream.read();
         } catch (IOException ex) {
            logger.severe("IO Exception while trying to read port of index "+i+".");
            angles[i] = 0;
         }
      }

      // Build the command object
      Command command = new Command(counter, angles);
      counter++;

      // Try to put object in queue
      boolean success = channel.offer(command);

      
      if(!success) {
         System.out.println("Dropped command "+command.getCounter());
      }
       
   }


   public void shutdown() {
      run = false;
      inputPort.close();
   }


   private void initializeInputStream() {
      // Get CommPortIdentifier
      CommPortIdentifier commPortId = null;

      try {
         commPortId = CommPortIdentifier.getPortIdentifier(portName);
      } catch (NoSuchPortException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

      // Get Serial Port
      inputPort = null;
      try {
         inputPort = (SerialPort) commPortId.open("Car Command", 4000);
      } catch (PortInUseException ex) {
         logger.log(Level.SEVERE, null, ex);
      }

      try {
         inputStream = inputPort.getInputStream();
      } catch (IOException ex) {
         logger.severe("Could not open InputStream for Car Input.");
      }


      // If InputStream could not be opened, shutdown.
/*
      if (inputStream == null) {
         shutdown();
      }
 */
   }

   /**
    * INSTANCE VARIABLES
    */
   private BlockingQueue<Command> channel;
   private SerialPort inputPort;
   private boolean run;
   InputStream inputStream;
   private int counter;
   private String portName;
   
   private Logger logger = Logger.getLogger(CarPadInput.class.getName());
   private final static int COMMAND_START = 255;
   private final static int INPUT_STREAM_READ_ERROR = -2;



}
