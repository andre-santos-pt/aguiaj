/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.grid;

import java.util.Arrays;

/**
 * Represents grids.
 * 
 * @author Andre L. Santos
 */
public class Grid {
	private final Position[][] positions;

	/**
	 * Constructs a grid.
	 * 
	 * @param rows Number of rows
	 * @param columns Number of columns
	 */
	public Grid(int rows, int columns) {
		if(rows < 1)
			throw new IllegalArgumentException("Number of rows should be greater than zero.");
		if(columns < 1)
			throw new IllegalArgumentException("Number of columns should be greater than zero.");

		positions = new Position[rows][columns];

		for(int row = 0; row < getNumberOfRows(); row++) {
			for(int col = 0; col < getNumberOfColumns(); col++) {
				positions[row][col] = createPosition(row, col);
			}
		}
	}			

	/**
	 * Designed for possible overriding
	 * @param row row index of the position (zero-based)
	 * @param column column index of the position (zero-based)
	 * @return the position object
	 */
	protected Position createPosition(int row, int column) {
		return new Position(row, column);
	}

	/**
	 * Are the position coordinates valid?
	 * @param row
	 * @param column
	 * @return true is the coordinates are within the grid dimensions, false otherwise.
	 */
	public boolean isValidPosition(int row, int column) {
		return 
		row >= 0 && row < getNumberOfRows() && 
		column >= 0 && column < getNumberOfColumns();
	}


	/**
	 * Obtains a position.
	 * @param row Row number
	 * @param column Column number
	 * @return The actual position object.
	 */
	public final Position getPosition(int row, int column) {
		if(row < 0 || row >= getNumberOfRows())
			throw new IllegalArgumentException("Invalid row: " + row);
		
		if(column < 0 || column >= getNumberOfColumns())
			throw new IllegalArgumentException("Invalid column: " + column);

		return positions[row][column];
	}

	/**
	 * Obtains a row.
	 * 
	 * @param row Row index
	 * @return An array with the actual <code>Position</code> objects that form the row
	 */
	public final Position[] getRow(int row) {
		if(row < 0 || row >= getNumberOfRows())
			throw new IllegalArgumentException("Invalid row");

		return Arrays.copyOf(positions[row], getNumberOfColumns());
	}

	/**
	 * Obtains a column.
	 * 
	 * @param column Column index
	 * @return An array with the actual <code>Position</code> objects that form the column
	 */
	public final Position[] getColumn(int column) {
		if(column < 0 || column >= getNumberOfColumns())
			throw new IllegalArgumentException("Invalid column");

		Position[] col = new Position[getNumberOfRows()];
		for(int i = 0; i < getNumberOfRows(); i++)
			col[i] = positions[i][column];

		return col;
	}

	/**
	 * Obtains all grid positions.
	 * 
	 * @return An array with the positions, line-by-line, left-to-right.
	 */
	public Position[] allPositions() {		
		Position[] ret = new Position[getNumberOfRows() * getNumberOfColumns()];
		for(int i = 0; i < getNumberOfRows(); i++)
			for(int j = 0; j < getNumberOfColumns(); j++)
				ret[i*getNumberOfColumns() + j] = positions[i][j];

		return ret;
	}

	/**
	 * Number of rows.
	 */
	public final int getNumberOfRows() {
		return positions.length;
	}

	/**
	 * Number of columns.
	 */
	public final int getNumberOfColumns() {
		return positions[0].length;
	}

	/**
	 * Clears all the positions.
	 */
	public void clearAll() {
		for(Position p : allPositions())
			p.clear();
	}
}
