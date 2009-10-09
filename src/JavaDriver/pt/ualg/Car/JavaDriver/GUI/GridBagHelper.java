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

// File   : layoutDemos/layoutDemoGB/GridBagHelper.java

import java.awt.GridBagConstraints;

// Purpose: Keeps track of current position in GridBagLayout.
//          Supports a few GridBag features: position, width, height, expansion.
//          All methods return GridBagHelper object for call chaining.
// Author : Fred Swartz - January 30, 2007 - Placed in public domain.


//////////////////////////////////////////////////////////////////// Class
public class GridBagHelper extends GridBagConstraints {

    //============================================================== constructor
    /**
     * Creates helper at top left, component always fills cells.
     */
    public GridBagHelper() {
        gridx = 0;
        gridy = 0;
        fill = GridBagConstraints.BOTH;  // Component fills area
    }

    //================================================================== nextCol
    /**
     * Moves the helper's cursor to the right one column.
     */
    public GridBagHelper nextCol() {
        gridx++;
        return this;
    }

    //================================================================== nextRow
    /**
     * Moves the helper's cursor to first col in next row.
     */
    public GridBagHelper nextRow() {
        gridx = 0;
        gridy++;
        return this;
    }

    //================================================================== expandW
    /**
     * Expandable Width.  Returns new helper allowing horizontal expansion.
     *  A new helper is created so the expansion values don't
     *  pollute the origin helper.
     */
    public GridBagHelper expandW() {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.weightx = 1.0;
        return duplicate;
    }

    //================================================================== expandH
    /** Expandable Height. Returns new helper allowing vertical expansion. */
    public GridBagHelper expandH() {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.weighty = 1.0;
        return duplicate;
    }

    //==================================================================== width
    /** Sets the width of the area in terms of number of columns. */
    public GridBagHelper width(int colsWide) {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.gridwidth = colsWide;
        return duplicate;
    }

    //==================================================================== width
    /** Width is set to all remaining columns of the grid. */
    public GridBagHelper width() {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.gridwidth = REMAINDER;
        return duplicate;
    }

    //=================================================================== height
    /** Sets the height of the area in terms of rows. */
    public GridBagHelper height(int rowsHigh) {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.gridheight = rowsHigh;
        return duplicate;
    }

    //=================================================================== height
    /** Height is set to all remaining rows. */
    public GridBagHelper height() {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.gridheight = REMAINDER;
        return duplicate;
    }

    //==================================================================== align
    /** Alignment is set by parameter. */
    public GridBagHelper align(int alignment) {
        GridBagHelper duplicate = (GridBagHelper)this.clone();
        duplicate.fill   = NONE;
        duplicate.anchor = alignment;
        return duplicate;
    }
}
