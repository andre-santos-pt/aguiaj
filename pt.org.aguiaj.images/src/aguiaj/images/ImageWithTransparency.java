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
package aguiaj.images;

/**
 * Represents images with transparency.
 */
public interface ImageWithTransparency extends Image {

	/**
	 * The opacity level at an image point (100 - fully opaque; 0 - transparent).
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * @return Opacity (range: 0-100)
	 */
	int getOpacity(int x, int y);
}
