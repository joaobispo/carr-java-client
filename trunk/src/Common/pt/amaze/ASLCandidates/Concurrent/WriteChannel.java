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

package pt.amaze.ASLCandidates.Concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper for a bounded write-only Blocking Queue.
 *
 * @author Joao Bispo
 */
public class WriteChannel<T> {

   public WriteChannel(int capacity) {
      channel = new ArrayBlockingQueue<T>(capacity);
      readPort = new ReadChannel(channel);
   }

   public ReadChannel<T> getReadChannel() {
      return readPort;
   }

   /**
    * Inserts the specified element into this queue if it is possible to do so
    * immediately without violating capacity restrictions, returning true upon
    * success and false if no space is currently available. When using a
    * capacity-restricted queue, this method is generally preferable to
    * BlockingQueue.add, which can fail to insert an element only by
    * throwing an exception.
    *
    * @param e the element to add
    * 
    * @return true if the element was added to this queue, else false
    */
   public boolean offer(T e) {
      return channel.offer(e);
   }

   public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
      return channel.offer(e, timeout, unit);
   }
   
   /**
    * Empties the channel.
    */
   public void clear() {
      T t = channel.poll();

      while(t != null) {
         t = channel.poll();
      }
   }

   /**
    * INSTANCE VARIABLES
    */
   private final BlockingQueue<T> channel;
   private final ReadChannel<T> readPort;
}
