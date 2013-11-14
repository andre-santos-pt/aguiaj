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
package aguiaj.iscte;

import java.util.Arrays;

import aguiaj.draw.IColor;
import aguiaj.draw.ITransparentImage;



/**
 * Represents color images with transparency, with constant dimension.
 * 
 * @author Andre L. Santos
 */
public class ColorImage implements ITransparentImage {
	
	private final Color[][] pixels;
	private final Dimension dimension;
	
	private int[][] alphaData;
	
	/**
	 * Constructs a color image with all pixels black.
	 * 
	 * @param width Image width
	 * @param height Image height
	 */
	public ColorImage(int width, int height) {
		if(!Dimension.isValidDimension(width, height))
			throw new IllegalArgumentException("Invalid dimensions - " + width + "x" + height);

		pixels = new Color[height][width];
		dimension = new Dimension(width, height);
	}
	

	/**
	 * Constructs a color image from a matrix.
	 * 1st dimension for row, 2nd dimension for column, 3rd dimension for RGB color (array of length 3)
	 * 
	 * @param matrix Image data
	 * @return A color image.
	 */
	public static ColorImage fromMatrix(int[][][] matrix) {
		if(matrix == null)
			throw new NullPointerException("Matrix cannot be null");

		if(matrix.length == 0)
			throw new IllegalArgumentException("Matrix must have at least one line");

		int cols = matrix[0].length;

		ColorImage img = new ColorImage(cols, matrix.length);
		for(int y = 0; y < img.getHeight(); y++) {
			if(matrix[y] == null)
				throw new IllegalArgumentException("Matrix line cannot be null (" + y + ")");

			if(matrix[y].length != cols)
				throw new IllegalArgumentException("Not a matrix (not all rows have the same length)");

			for(int x = 0; x < img.getWidth(); x++) {			
				if(matrix[y][x] == null) {
					img.setColor(x, y, Color.BLACK);
				}
				else {
					if(matrix[y][x].length != 3)
						throw new IllegalArgumentException("Matrix 2nd dimension values have to have length equals 3");

					img.setColor(x, y, new Color(
							matrix[y][x][0], 
							matrix[y][x][1], 
							matrix[y][x][2]));
				}
			}
		}
		return img;
	}
	
	/**
	 * Image dimension.
	 */
	@Override
	public Dimension getDimension() {
		return dimension;
	}
	
	/**
	 * Image width.
	 * @return a positive integer.
	 */
	public int getWidth() {
		return pixels[0].length;
	}

	/**
	 * Image width.
	 * @return a positive integer.
	 */
	public int getHeight() {
		return pixels.length;
	}
	
	/**
	 * Color of pixel (x, y).
	 */
	@Override
	public Color getColor(int x, int y) {
		dimension.validatePointArguments(x, y);
		return pixels[y][x] == null ? Color.WHITE : pixels[y][x];
	}
	
	/**
	 * Opacity of pixel (x, y).
	 */
	@Override
	public int getOpacity(int x, int y) {
		return alphaData == null ? 100 : alphaData[y][x];
	}
	
	/**
	 * Sets the color of a pixel.
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * @param color Color
	 */
	public void setColor(int x, int y, IColor color) {
		dimension.validatePointArguments(x, y);
		if(color == null)
			throw new NullPointerException("Color cannot be null");
		
		pixels[y][x] = new Color(color.getR(), color.getG(), color.getB());
	}
	
	/**
	 * Sets the transparency of the image, given an integer matrix with values in the range [0,100]
	 * @param transparency not-null reference to an integer matrix with the same dimension as the image
	 */
	public void setOpacity(int[][] transparency) {
		if(transparency == null) {
			alphaData = null;
		}
		else {
			if(transparency.length != getHeight())
				throw new IllegalArgumentException("The number of rows must be equal to the image height");
	
			for(int[] row : transparency)
				if(row.length != getWidth())
					throw new IllegalArgumentException("The length of every row must be equal to the image width");
	
			if(alphaData == null)
				alphaData = new int[getHeight()][getWidth()];
	
			int i = 0;
			for(int[] row : transparency) {				
				alphaData[i] = Arrays.copyOf(row, getWidth());
				i++;
			}
		}
	}
}
