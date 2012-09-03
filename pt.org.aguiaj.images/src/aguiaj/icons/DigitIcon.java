/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package aguiaj.icons;


import pt.org.aguiaj.extensibility.AguiaJHelper;
import aguiaj.colors.Color;
import aguiaj.images.ColorImage;
import aguiaj.images.ImageUtils;
import aguiaj.images.ImageWithTransparency;

/**
 * Represents digit icons (0-9).
 * 
 * @author Andre L. Santos
 */
public enum DigitIcon implements ImageWithTransparency {
	
	ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE;

	private ColorImage icon;

	private DigitIcon() {
		icon = ImageUtils.fromSwtImage(AguiaJHelper.getPluginImage(name()));
	}

	public static DigitIcon get(int number) {
		if(number < 0 || number > 9)
			throw new IllegalArgumentException("Only digits are allowed (0-9)");
		
		return values()[number];
	}

	@Override
	public int getWidth() {
		return icon.getWidth();
	}

	@Override
	public int getHeight() {
		return icon.getHeight();
	}

	@Override
	public Color getColor(int x, int y) {
		return icon.getColor(x, y);
	}

	@Override
	public int getOpacity(int x, int y) {
		return icon.getOpacity(x, y);
	}
}
