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

package pt.ualg.Car.JavaDriver.GUI;

// File   : layoutDemos/layoutDemoGB/Gap.java

import java.awt.Dimension;
import javax.swing.JComponent;

// Purpose: Gaps for use in GridBagLayout (or any other).
//          Library alternatives are available in the Box class.
// Author : Fred Swartz - January 30, 2007 - Placed in public domain.

////////////////////////////////////////////////////////////////////// Class Gap
public class Gap extends JComponent {

    //============================================================== constructor
    /* Creates filler with minimum size, but expandable infinitely. */
    public Gap() {
        Dimension min = new Dimension(0, 0);
        Dimension max = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        setMinimumSize(min);
        setPreferredSize(min);
        setMaximumSize(max);
    }

    //============================================================== constructor
    /* Creates rigid filler. */
    public Gap(int size) {
        Dimension dim = new Dimension(size, size);
        setMinimumSize(dim);
        setPreferredSize(dim);
        setMaximumSize(dim);
    }
}

