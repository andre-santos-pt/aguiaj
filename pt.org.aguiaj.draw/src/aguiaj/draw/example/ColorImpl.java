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
package aguiaj.draw.example;

/**
 * Represents RGB colors.
 * 
 * @author Andre L. Santos
 */
public class ColorImpl {
	/**
	 * White color.
	 */
	public static final ColorImpl WHITE = new ColorImpl(255, 255, 255);
	
	/**
	 * Silver color.
	 */
	public static final ColorImpl SILVER = new ColorImpl(192, 192, 192);
	
	/**
	 * Gray color.
	 */
	public static final ColorImpl GRAY = new ColorImpl(128, 128, 128);
	
	/**
	 * Black color.
	 */
	public static final ColorImpl BLACK = new ColorImpl(0, 0, 0);
	
	/**
	 * Red color.
	 */
	public static final ColorImpl RED = new ColorImpl(255, 0, 0);
	
	/**
	 * Maroon color.
	 */
	public static final ColorImpl MAROON = new ColorImpl(128, 0, 0);
	
	/**
	 * Yellow color.
	 */
	public static final ColorImpl YELLOW = new ColorImpl(255, 255, 0);
	
	/**
	 * Olive color.
	 */
	public static final ColorImpl OLIVE = new ColorImpl(128, 128, 0);
	
	/**
	 * Lime color.
	 */
	public static final ColorImpl LIME = new ColorImpl(0, 255, 0);
	
	/**
	 * Green color.
	 */
	public static final ColorImpl GREEN= new ColorImpl(0, 128, 0);
	
	/**
	 * Aqua color.
	 */
	public static final ColorImpl AQUA = new ColorImpl(0, 255, 255);
	
	/**
	 * Teal color.
	 */
	public static final ColorImpl TEAL = new ColorImpl(0, 128, 128);
	
	/**
	 * Blue color.
	 */
	public static final ColorImpl BLUE = new ColorImpl(0, 0, 255);
	
	/**
	 * Navy color.
	 */
	public static final ColorImpl NAVY = new ColorImpl(0, 0, 128);
	
	/**
	 * Fuchsia color.
	 */
	public static final ColorImpl FUCHSIA = new ColorImpl(255, 0, 255);
	
	/**
	 * Purple color.
	 */
	public static final ColorImpl PURPLE = new ColorImpl(128, 0, 128);
	
	private final int r;
	private final int g;
	private final int b;
	
	/**
	 * Constructs a color based on a RGB triplet.
	 * @param r Red value
	 * @param g Green value
	 * @param b Blue value
	 */
	public ColorImpl(int r, int g, int b) {
		checkRange(r);
		checkRange(g);
		checkRange(b);
	
		this.r = r;
		this.g = g;
		this.b = b;	
	}
	
	/**
	 * Creates a gray tone (Range: [0-255])
	 * @param value Tone value
	 * @return a grayscale color
	 */
	public static ColorImpl createGraytone(int value) {
		checkRange(value);
		return new ColorImpl(value, value, value);
	}
	
	/**
	 * Red value.
	 * @return An integer within the range [0-255]
	 */
	public int getR() {
		return r;
	}

	/**
	 * Green value.
	 * @return An integer within the range [0-255]
	 */
	public int getG() {
		return g;
	}

	/**
	 * Blue value.
	 * @return An integer within the range [0-255]
	 */
	public int getB() {
		return b;
	}
	
	/**
	 * Obtains the luminance of the color (a gray tone).  
	 * @return an integer within [0, 255]
	 */
	public int getLuminance() {
		return (int) Math.round(0.3*r + 0.59*g + 0.11*b);
	}
	
	/**
	 * Obtain a color by converting to gray tone.
	 * @return a grayscale color
	 */
	public ColorImpl toGraytone() {
		if(r == g && g == b)
			return this;
		else
			return createGraytone(getLuminance());
	}
	
	/**
	 * Validate R/G/B value [0-255] 
	 * @param v integer to validate
	 */
	private static void checkRange(int v) {
		if(v < 0 || v > 255)
			throw new IllegalArgumentException("Invalid value: " + v + " (Valid range: 0-255)");
	}
	
	@Override
	public boolean equals(Object object) {
		return
		object instanceof ColorImpl &&
		((ColorImpl) object).getR() == r &&
		((ColorImpl) object).getG() == g &&
		((ColorImpl) object).getB() == b;
		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + r;
		result = 31 * result + g;
		result = 31 * result + b;
		return result;
	}
	
	@Override
	public String toString() {
		return r + " " + g + " " + b;
	}
}
