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

package pt.ualg.AldricCar.CarClient.ClientModule;

import pt.amaze.ASLCandidates.Identification.IntIdentifier;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.Command;
import pt.ualg.AldricCar.CarClient.CommunicationsModule.CommandSource;

/**
 *
 * @author Joao Bispo
 */
public class CarpadCommandSource implements CommandSource {

   public boolean connect() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void disconnect() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public boolean isConnected() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public long commandPeriod() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public Command readCommand() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setTimeout(long timeout) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public long getTimeout() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setIntIdentifier() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public IntIdentifier getIntIdentifier() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

}
