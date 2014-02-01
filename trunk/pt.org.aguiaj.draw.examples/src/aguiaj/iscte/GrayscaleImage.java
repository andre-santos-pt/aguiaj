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
package aguiaj.iscte;


import aguiaj.draw.IImage;


/**
 * Represents grayscale images with constant dimension.
 * 
 * @author Andre L. Santos
 */
public class GrayscaleImage implements IImage {
	
	private final int[][] pixels;
	private final Dimension dimension;
	
	/**
	 * Constructs a grayscale image with all pixels black.
	 * @param width
	 * @param height
	 */
	public GrayscaleImage(int width, int height) {
		if(!Dimension.isValidDimension(width, height))
			throw new IllegalArgumentException("Invalid dimensions - " + width + "x" + height);
		
		pixels = new int[height][width];
		dimension = new Dimension(width, height);
	}
	
	
	/**
	 * Creates a grayscale image from an integer matrix
	 * @param pixels a valid matrix
	 */
	public static GrayscaleImage create(int[][] pixels) {
		if(!validMatrix(pixels))
			throw new IllegalArgumentException("invalid matrix for grayscale image");
		
		GrayscaleImage img = new GrayscaleImage(pixels[0].length, pixels.length);
		
		for(int y = 0; y < pixels.length; y++)
			for(int x = 0; x < pixels[y].length; x++)
				img.setGraytone(x, y, pixels[y][x]);
		
		return img;
	}

	/**
	 * Verifies if the matrix is valid as data for an image, i.e. lines with equals length and all values in the range [0, 255]
	 */
	public static boolean validMatrix(int[][] pixels) {
		if(pixels == null)
			return false;
		
		for(int i = 0; i < pixels.length-1; i++) {
			if(pixels[i] == null || pixels[i].length != pixels[i+1].length)
				return false;
		
			for(int v : pixels[i])
				if(!validTone(v))
					return false;
		}
		
		return true;
	}
	
	private static boolean validTone(int v) {
		return v >=0 && v <=255;
	}
	
	/**
	 * Image dimension.
	 */
	@Override
	public Dimension getDimension() {
		return dimension;
	}
	
	/**
	 * Color of pixel (x, y).
	 */
	@Override
	public Color getColor(int x, int y) {
		dimension.validatePointArguments(x, y);
		return Color.createGraytone(pixels[y][x]);
	}
	
	/**
	 * Image width.
	 * @return a positive integer
	 */
	public int getWidth() {
		return dimension.getWidth();
	}
	
	/**
	 * Image height.
	 * @return a positive integer
	 */
	public int getHeight() {
		return dimension.getHeight();
	}
	
	/**
	 * Graytone of pixel (x,y)
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * @return an integer in the range [0-255]
	 */
	public int getGraytone(int x, int y) {
		dimension.validatePointArguments(x, y);
		return pixels[y][x];
	}
	
	/**
	 * Sets the graytone of pixel (x,y)
	 * @param x
	 * @param y
	 * @param tone
	 */
	public void setGraytone(int x, int y, int tone) {
		dimension.validatePointArguments(x, y);
		if(!validTone(tone))
			throw new IllegalArgumentException("invalid tone: " + tone);
		
		pixels[y][x] = tone;
	}
}
