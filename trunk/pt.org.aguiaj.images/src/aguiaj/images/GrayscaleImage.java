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

import aguiaj.colors.Color;

/**
 * Represents black and white images.
 * 
 * @author Andre L. Santos
 */
public class GrayscaleImage implements Image {

	private final int[][] pixels;
	
	/**
	 * Constructs a blank black and white image.
	 * 
	 * @param width Image width
	 * @param height Image height
	 */
	public GrayscaleImage(int width, int height) {
		if(!ImageUtils.isValidDimension(width, height))
			throw new IllegalArgumentException("Invalid dimensions - " + width + "x" + height);

		pixels = new int[height][width];
	}
	
	/**
	 * Constructs a black and white image from a matrix.
	 * 1st dimension for row, 2nd dimension for column. 
	 * Grayscale value range: [0-255]
	 * @param matrix Image data
	 */
	public static GrayscaleImage fromMatrix(int[][] matrix) {
		if(matrix == null)
			throw new NullPointerException("Matrix cannot be null");

		if(matrix.length == 0)
			throw new IllegalArgumentException("Matrix must have at least one line");

		int cols = matrix[0].length;

		GrayscaleImage img = new GrayscaleImage(cols, matrix.length);
		for(int y = 0; y < img.getHeight(); y++) {
			if(matrix[y] == null)
				throw new IllegalArgumentException("Matrix line cannot be null (" + y + ")");

			if(matrix[y].length != cols)
				throw new IllegalArgumentException("Not a matrix (not all rows have the same length)");

			for(int x = 0; x < img.getWidth(); x++) {		
				if(matrix[y][x] >= 0 && matrix[y][x] <= 255)
					img.setGraytone(x, y, matrix[y][x]);
				else
					throw new IllegalArgumentException("Matrix 2nd dimension values have to within the valid range ([0-255])");
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
		
		return Color.createGraytone(pixels[y][x]);		
	}

	/**
	 * Sets the gray tone of a pixel.
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * @param tone Gray tone within the range [0-255]
	 */
	public void setGraytone(int x, int y, int tone) {
		if(!ImageUtils.isValidPoint(x, y, this))
			throw new IllegalArgumentException("Invalid point - (" + x + ", " + y + ")");

		if(tone < 0 || tone > 255)
			throw new IllegalArgumentException("Invalid tone (valid range: [0-255])");

		pixels[y][x] = tone;
	}
	
	
	/**
	 * Obtains the gray tone of a pixel.
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * @return an integer within the range [0-255]
	 */
	public int getGraytone(int x, int y) {
		return pixels[y][x];
	}
	
}
