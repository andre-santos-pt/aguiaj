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
 * Represents RGB colors.
 * 
 * @author Andre L. Santos
 */
public interface Color {
	
	/**
	 * Red value.
	 * @return An integer within the range [0-255]
	 */
	int getR();
	
	/**
	 * Green value.
	 * @return An integer within the range [0-255]
	 */
	public int getG();
	
	/**
	 * Blue value.
	 * @return An integer within the range [0-255]
	 */
	public int getB();
}
