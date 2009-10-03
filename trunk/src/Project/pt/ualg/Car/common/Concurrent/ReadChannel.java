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

package pt.ualg.Car.common.Concurrent;

import java.util.concurrent.BlockingQueue;

/**
 *
 * @author Joao Bispo
 */
public class ReadChannel<T> {

   public ReadChannel(BlockingQueue<T> channel) {
      this.channel = channel;
   }


   /**
    * Retrieves and removes the head of this queue, or returns null if this queue is empty.
    * 
    * @return the head of this queue, or null if this queue is empty
    */
   public T poll() {
      return channel.poll();
   }

   /**
    * Retrieves and removes the head of this queue, waiting if necessary until
    * an element becomes available.
    *
    * @return the head of this queue
    * @throws InterruptedException if interrupted while waiting
    */
   public T take() throws InterruptedException {
      return channel.take();
   }

   /**
    * INSTANCE VARIABLES
    */
   private final BlockingQueue<T> channel;
}
