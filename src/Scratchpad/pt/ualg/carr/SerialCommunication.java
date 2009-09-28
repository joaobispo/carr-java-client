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

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.app.Preferences;

/**
 *
 * @author Joao Bispo
 */
public class SerialCommunication {

   public void run() throws Exception {
      {

         CommPortIdentifier commPort = getCommPort();

         InputStream input = null;

         System.out.println(commPort.getName());

         
      
            
            // Setup it
            SerialPort port = (SerialPort) commPort.open("serial madness", 4000);

            input = port.getInputStream();
            System.out.println("BufferSize:"+port.getInputBufferSize());

            System.out.println(System.getProperty("os.name","").toLowerCase());

            //OutputStream output = port.getOutputStream();


            /*
            port.setSerialPortParams(
                    9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
*/
            boolean run = true;

            //processInput(input);

            //while(run) {
            //   while (input.available()>0)
		//		System.out.println((input.read()));
            //}


          port.close();

      }

   }

   public void runWithProcessing() {
      Preferences p = new Preferences();

      String port = Preferences.get("serial.port");
      System.out.println("Port:"+port);

      /*
		System.out.println("Using port: " + Preferences.get("serial.port"));
		CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(
				Preferences.get("serial.port"));
		SerialPort port = (SerialPort)portId.open("serial madness", 4000);
        input = port.getInputStream();
        output = port.getOutputStream();
        port.setSerialPortParams(
			      Preferences.getInteger("serial.debug_rate"),
			      SerialPort.DATABITS_8,
			      SerialPort.STOPBITS_1,
			      SerialPort.PARITY_NONE);
		while(true){
			while (input.available()>0)
				System.out.print((char)(input.read()));
		}
       */
   }

   /**
    * 
    * @param comPorts
    * @return
    */
   private CommPortIdentifier chooseComPort(List<CommPortIdentifier> comPorts) {
      // Check if any of the ports is the default port name
      for(CommPortIdentifier comPort : comPorts) {
         if(comPort.getName().equals(serialPortDefaultName)) {
            return comPort;
         }
      }

      // If it has not returned, didn't find the default comm port.
      // Ask the user
      System.out.println("Choose between one of the following Comm ports:");
      for(CommPortIdentifier comPort : comPorts) {
         System.out.println(comPort.getName());
      }

      return null;
   }

   private String serialPortDefaultName = "COM4";

   private CommPortIdentifier getCommPort() {
            // Get All Comm Ports
            Enumeration<CommPortIdentifier> e = CommPortIdentifier.getPortIdentifiers();
            List<CommPortIdentifier> commPorts = new ArrayList<CommPortIdentifier>();
            while (e.hasMoreElements()) {
               commPorts.add(e.nextElement());
            }

            // Find the Comm port we want
            int numCommPorts = commPorts.size();
            CommPortIdentifier commPort = null;
            if (numCommPorts > 1) {
               commPort = chooseComPort(commPorts);
            } else {
               commPort = commPorts.get(0);
            }

            return commPort;
   }

   private void processInput(InputStream input) throws Exception {


      // Wait for the stream
      PadInputs inputs = new PadInputs(input);
      long sleepTime = 40;

      while(true) {
         inputs.update();
         System.out.println(inputs.toString());
         Thread.sleep(sleepTime);
      }
      
   }
}
