/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.images;

import java.util.Arrays;

import aguiaj.colors.Color;


/**
 * Represents color images with optional transparency.
 * 
 * @author Andre L. Santos
 */
public class ColorImage implements ImageWithTransparency {
	
	private Color[][] pixels;
	private int[][] alphaData;
	
	/**
	 * Constructs a blank color image.
	 * 
	 * @param width Image width
	 * @param height Image height
	 */
	public ColorImage(int width, int height) {
		if(!ImageUtils.isValidDimension(width, height))
			throw new IllegalArgumentException("Invalid dimensions - " + width + "x" + height);

		pixels = new Color[height][width];
		ImageUtils.setAll(pixels, Color.WHITE);
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
							matrix[y][x][ImageUtils.R], 
							matrix[y][x][ImageUtils.G], 
							matrix[y][x][ImageUtils.B]));
				}
			}
		}
		return img;
	}
	
	@Override
	public int getWidth() {
		return pixels[0].length;
	}

	@Override
	public int getHeight() {
		return pixels.length;
	}
	
	@Override
	public Color getColor(int x, int y) {
		if(!ImageUtils.isValidPoint(x, y, this))
			throw new IllegalArgumentException("Invalid point - (" + x + ", " + y + ")");

		return pixels[y][x];
	}
	
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
	public void setColor(int x, int y, Color color) {
		if(!ImageUtils.isValidPoint(x, y, this))
			throw new IllegalArgumentException("Invalid point - (" + x + ", " + y + ")");

		if(color == null)
			throw new NullPointerException("Color cannot be null");
		
		pixels[y][x] = color;
	}
	
	public void setOpacity(int[][] transparencyData) {
		if(transparencyData == null) {
			alphaData = null;
		}
		else {
			if(transparencyData.length != getHeight())
				throw new IllegalArgumentException("The number of rows must be equal to the image height");
	
			for(int[] row : transparencyData)
				if(row.length != getWidth())
					throw new IllegalArgumentException("The length of every row must be equal to the image width");
	
			if(alphaData == null)
				alphaData = new int[getHeight()][getWidth()];
	
			int i = 0;
			for(int[] row : transparencyData) {				
				alphaData[i] = Arrays.copyOf(row, getWidth());
				i++;
			}
		}
	}
	
}
