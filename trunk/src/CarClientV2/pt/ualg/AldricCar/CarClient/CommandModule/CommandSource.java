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

package pt.ualg.AldricCar.CarClient.CommandModule;

import pt.amaze.ASLCandidates.Identification.IntIdentifier;

/**
 * Generates Command objects at a periodic rate.
 *
 * <p>If a Command is created and the previous Command wasn't read yet, the new
 * Command object is discarded.
 *
 * @author Joao Bispo
 */
public interface CommandSource {

   /**
    * If object is not connected, attempts to connect the CommandSource,
    * so it can send commands. If it is already connected, nothing happens.
    *
    * <p> When connected, CommandSource generates Command
    * objects periodically.
    * 
    * @return true if a connection could be made and Commands are being created.
    * Returns false otherwise.
    */
   boolean connect();
   
   /**
    *  If connected, disconnects the CommandSource so it stops sending commands,
    *  and frees all resources it requested when connected.
    * <br> If object is not connected, nothing happens.
    */
   void disconnect();
   
   /**
    * 
    * @return true if the command source is currently sending commands. 
    * Returns false otherwise.
    */
   boolean isConnected();

   
   /**
    * When a CommandSource is connected, it builds Command objects at a fixed 
    * rate, approximately. 
    * 
    * @return the period at which CommandSource generates Command objects when
    * connected, in milliseconds.
    */
   long commandPeriod();

   /**
    * Reads a Command object from the CommandSource.
    *
    * <p>If there is no Command object available, the method blocks until there
    * is a Command object available, or until it times out.  If it times out,
    * or CommandSource is not connected, returns a special Command with
    * invalid status.
    *
    * @return a Command object.
    */
   Command readCommand();

   /**
    *  Sets the value of the timeout, in milliseconds, when reading a Command.
    * 
    * @param timeout
    */
   void setTimeout(long timeout);

   /**
    *
    * @return the value of the timeout, in milliseconds, when readCommand() is
    * invoked.
    */
   long getTimeout();
   
   /**
    * Sets the IntIdentifier of this CommandSource, needed to generate unique
    * Command objects.
    */
   void setIntIdentifier();
   
   /**
    * @return the IntIdentifier of this CommandSource.
    */
   IntIdentifier getIntIdentifier();

}
