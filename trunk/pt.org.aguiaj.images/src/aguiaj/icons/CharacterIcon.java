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
package aguiaj.icons;


import pt.org.aguiaj.extensibility.AguiaJHelper;
import aguiaj.colors.Color;
import aguiaj.images.ColorImage;
import aguiaj.images.ImageUtils;
import aguiaj.images.ImageWithTransparency;

/**
 * Represents the icons of a few famous characters.
 * 
 * @author Andre L. Santos
 */
public enum CharacterIcon implements ImageWithTransparency {
	
	SMILEY("smiley"),
	SUPER_MARIO("supermario"),
	SONIC("sonic"),
	MAGGIE("maggie"),
	BART("bart"),
	AGUIAJ("aguiaj");

	private ColorImage icon;

	private CharacterIcon(String path) {
		icon = ImageUtils.fromSwtImage(AguiaJHelper.getPluginImage(path));
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
