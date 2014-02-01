/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package aguiaj.draw;


/**
 * Represents two-dimensional grids, where each position has a background color, and optionally an image.
 * @author Andre L. Santos
 */
public interface IGrid  {

	/**
	 * The grid dimension.
	 * @return a non-null reference
	 */
	IDimension getDimension();

	/**
	 * Number of pixels of the side length of each grid position.
	 * @return a positive integer
	 */
	int getPositionPixels();
	
	/**
	 * Returns the background color at a given grid position. Cannot return null.
	 * 
	 * @param row a valid index of the position's row according to the grid dimension
	 * @param column a valid index of the position's column according to the grid dimension
	 * @return a non-null reference
	 */
	IColor getBackground(int row, int column);
	
	/**
	 * Returns the image at a given grid position. May return null, meaning that there is no image at the position.
	 * @param row a valid index of the position's row according to the grid dimension
	 * @param column a valid index of the position's column according to the grid dimension
	 * @return a reference to an image, or null in case there is no image at the position
	 */
	IImage getImageAt(int row, int column);
}
