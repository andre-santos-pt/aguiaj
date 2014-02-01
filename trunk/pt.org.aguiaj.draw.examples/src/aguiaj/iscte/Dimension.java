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

import aguiaj.draw.IDimension;


/**
 * Represents constant rectangular dimensions.
 * 
 * @author Andre L. Santos
 */
public final class Dimension implements Comparable<Dimension>, IDimension {

	private final int width;
	private final int height;
	
	/**
	 * Constructs a dimension given width and height.
	 * 
	 * @param width a positive integer
	 * @param height a positive integer
	 */
	public Dimension(int width, int height) {
		if(!isValidDimension(width, height))
			throw new IllegalArgumentException("width and height must be greater than zero");
		
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Are the values width/height valid for a dimension?
	 */
	public static boolean isValidDimension(int width, int height) {
		return width > 0 && height > 0;
	}

	/**
	 * Dimension width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Dimension height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Area (width*height).
	 */
	public int getArea() {
		return width * height;
	}
	
	/**
	 * Is the dimension squared?
	 */
	public boolean isSquare() {
		return width == height;
	}
	
	/**
	 * Scale the dimension by a given factor.
	 * @param factor a positive double
	 * @return a dimension scaled by the factor (width*factor, height*factor)
	 */
	public Dimension scale(double factor) {
		if(factor <= 0.0)
			throw new IllegalArgumentException("factor must be greater than zero");
		
		return new Dimension((int) Math.round(width * factor), (int) Math.round(height * factor));
	}
	
	/**
	 * Is (x, y) a valid point for this dimension?
	 */
	public boolean isValidPoint(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	/**
	 * Convenience method for validating a point. 
	 * @throws IllegalArgumentException if the point is not valid for the dimension.
	 */
	public void validatePointArguments(int x, int y) {
		if(!isValidPoint(x, y))
			throw new IllegalArgumentException("Invalid point - (" + x + ", " + y + ")");
	}
		
	
	
	
	@Override
	public String toString() {
		return width + " x " + height;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;
		
		Dimension other = (Dimension) obj;
		return height == other.height && width == other.width;
	}

	@Override
	public int compareTo(Dimension d) {
		if(d == null)
			throw new NullPointerException("argument cannot be null");
		
		return new Integer(getArea()).compareTo(d.getArea());
	}
}
