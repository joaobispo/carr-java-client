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

package pt.amaze.ASLCandidates;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility methods for RxTx Library (Serial and Parallel Port)
 *
 * @author Joao Bispo
 */
public class RxtxUtils {

   /**
    * Tries to opens the serial port with name “portName”.
    * If a connection is not possible, the event is logged.
    *
    * <p>It uses as parameters:
    * <br> Baudrate: 9600;
    * <br> Databits: 8;
    * <br> StopBits: 1;
    * <br> Parity: None;
    * 
    * @param portName the name of the serial port
    * @param appName used to identify which application is connected to the port
    * @return a SerialPort object with a sucessful connection. If a connection 
    * was not possible, null is returned.
    */
   public static SerialPort openSerialPort(String portName, String appName) {
      SerialPort serialPort = null;
      // How much to wait for opening the port
      int millisWait = 500;
      // How much to wait for receiving the inputstream
      int inputStreamReadTimeoutMillis = 2000;

      try {
         // Obtain a CommPortIdentifier object for the port you want to open
         CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);

         // Get the port's ownership
         serialPort = (SerialPort) portId.open(appName, millisWait);

         // Setup the Serial Port
         serialPort.setSerialPortParams(9600,
                 SerialPort.DATABITS_8,
                 SerialPort.STOPBITS_1,
                 SerialPort.PARITY_NONE);


         serialPort.enableReceiveTimeout(inputStreamReadTimeoutMillis);

      } catch (NoSuchPortException ex) {
         Logger.getLogger(RxtxUtils.class.getName()).
                 info("Serial Port '" + portName + "' not found.");
         return null;
      } catch (PortInUseException ex) {
         Logger.getLogger(RxtxUtils.class.getName()).
                 info("Serial Port '" + portName + "' is already in use.");
         return null;
      } catch (UnsupportedCommOperationException e) {
         Logger.getLogger(RxtxUtils.class.getName()).
                 warning("Serial Port '" + portName + "' could not be setup.");
         return null;
      }

      if(serialPort == null) {
         Logger.getLogger(RxtxUtils.class.getName()).
                 warning("Could not connect to Serial Port '"+portName+"'.");
         return null;
      }

      return serialPort;
   }


   /**
    * @return a list of Strings with the names of all the serial ports it could
    * find in the system.
    */
   public static List<String> getSerialPortList() {
      Enumeration ports = CommPortIdentifier.getPortIdentifiers();
      List<String> portList = new ArrayList<String>();

      while (ports.hasMoreElements()) {
         CommPortIdentifier port = (CommPortIdentifier) ports.nextElement();
         if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
            portList.add(port.getName());
         }
      }

      return portList;
   }

   /**
    * Tests for the presence of RxTx dynamic libraries.
    * Currently, test only supports Windows.
    *
    * @return true, if it can find the libraries for the corresponding operating
    * system. If an unsupported operating system is detected, returns true.
    */
    public static boolean rxtxLibrariesExists() {
       Logger logger = Logger.getLogger(RxtxUtils.class.getName());

       String os = System.getProperty("os.name");
        String lowerOs = os.toLowerCase();

        if(lowerOs.startsWith("windows")) {
            // Check for DLLs
            File rxtxSerial = new File("rxtxSerial.dll");
            if(!rxtxSerial.exists()) {
               logger.warning("Could not find file rxtxSerial.dll.");
                return false;
            }
        } else {
            logger.info("Test for RxTx libraries not supported on operating system '"+os+"'.");
            return true;
        }

        return true;
    }

}
