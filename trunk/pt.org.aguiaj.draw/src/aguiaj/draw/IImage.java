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
package aguiaj.draw;



/**
 * Represents images.
 * 
 * @author Andre L. Santos
 */
public interface IImage {
	
	/**
	 * Constant
	 * @return
	 */
	IDimension getDimension();
	
	/**
	 * The color at image point (x, y).
	 * @param x a valid x-axis coordinate according to the image dimension
	 * @param y a valid y-axis coordinate according to the image dimension
	 */
	IColor getColor(int x, int y);
}
