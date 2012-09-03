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
 * Represents the icons of the basic latin alphabet letters.
 *  
 * @author Andre L. Santos
 */
public enum LetterIcon implements ImageWithTransparency {
	A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z;

	private ColorImage icon;

	private LetterIcon() {
		icon = ImageUtils.fromSwtImage(AguiaJHelper.getPluginImage(name()));
	}
	
	public static LetterIcon get(char letter) {
		if(letter < 'a' || letter > 'z')
			throw new IllegalArgumentException("Invalid letter (valid range: 'a' to 'z'");
		
		return values()[letter - 'a'];
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
