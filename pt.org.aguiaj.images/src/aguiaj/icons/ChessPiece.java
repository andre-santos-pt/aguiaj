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
package aguiaj.icons;


import pt.org.aguiaj.extensibility.AguiaJHelper;
import aguiaj.images.Image;
import aguiaj.images.ImageUtils;

/**
 * Represents the chess pieces (on white and black)
 *
 * @author Andre L. Santos
 */
public enum ChessPiece {	
	PONE,
	CASTLE,
	KNIGHT,
	BISHOP,
	QUEEN,
	KING;
	
	public Image getWhite() {
		return ImageUtils.fromSwtImage(AguiaJHelper.getPluginImage(name() + "_" + "WHITE"));
	}

	public Image getBlack() {
		return ImageUtils.fromSwtImage(AguiaJHelper.getPluginImage(name() + "_" + "BLACK"));
	}	
}
