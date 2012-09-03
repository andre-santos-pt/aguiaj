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
package aguiaj.images;

import aguiaj.colors.Color;


/**
 * Represents images.
 * 
 * @author Andre L. Santos
 */
public interface Image {
	/**
	 * THe image width in pixels.
	 */
	int getWidth();
	
	/**
	 * The image height in pixels.
	 */
	int getHeight();
	
	/**
	 * The color at image point (<i>x</i>, <i>y</i>).
	 */
	Color getColor(int x, int y);
}
