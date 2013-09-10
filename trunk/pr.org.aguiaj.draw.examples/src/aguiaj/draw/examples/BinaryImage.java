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
package aguiaj.draw.examples;

import aguiaj.draw.Dimension;
import aguiaj.draw.Image;



/**
 * Represents binary images, where each pixel only has two possible values (black or white).
 * 
 * @author Andre L. Santos
 */
public class BinaryImage implements Image {
	
	private final boolean[][] pixels;
	
	private final Dimension dimension;
	
	/**
	 * Constructs a Binary Image given a dimension.
	 * 
	 * @param width Image width
	 * @param height Image height
	 */
	public BinaryImage(int width, int height) {
		if(!Dimension.isValidDimension(width, height))
			throw new IllegalArgumentException("Invalid dimensions - " + width + "x" + height);

		pixels = new boolean[height][width];
		dimension = new Dimension(width, height);
	}
	
	/**
	 * Constructs a binary image from a boolean matrix.
	 * 1st dimension for row, 2nd dimension for column. 
	 * <code>false</code> represents white, whereas <code>true</code> represents black.
	 * @param matrix Image data
	 */
	public static BinaryImage fromMatrix(boolean[][] matrix) {
		if(matrix == null)
			throw new NullPointerException("Matrix cannot be null");

		if(matrix.length == 0)
			throw new IllegalArgumentException("Matrix must have at least one line");

		int cols = matrix[0].length;

		BinaryImage img = new BinaryImage(cols, matrix.length);
		for(int y = 0; y < img.getDimension().getHeight(); y++) {
			if(matrix[y] == null)
				throw new IllegalArgumentException("Matrix line cannot be null (" + y + ")");

			if(matrix[y].length != cols)
				throw new IllegalArgumentException("Not a matrix (not all rows have the same length)");

			for(int x = 0; x < img.getDimension().getWidth(); x++) {		
				if(matrix[y][x])
					img.setBlack(x, y);				
			}
		}
		return img;
	}
	
	@Override
	public Color getColor(int x, int y) {
		validatePoint(x, y);
		return pixels[y][x] ? Color.BLACK : Color.WHITE;
	}
	
	
	/**
	 * Sets a pixel black.
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 */
	public void setBlack(int x, int y) {
		validatePoint(x, y);
		pixels[y][x] = true;
	}
	
	/**
	 * Sets a pixel white.
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 */
	public void setWhite(int x, int y) {
		validatePoint(x, y);
		pixels[y][x] = false;
	}
	
	/**
	 * Is the pixel black? 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * 
	 * @return <code>true</code> if yes, <code>false</code> otherwise
	 */
	public boolean isBlack(int x, int y) {
		validatePoint(x, y);
		return pixels[y][x];
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}

	@Override
	public Image copy() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void validatePoint(int x, int y) {
		if(!dimension.isValidPoint(x, y))
			throw new IllegalArgumentException("Invalid point - (" + x + ", " + y + ")");
	}
}

