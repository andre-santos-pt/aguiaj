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
package aguiaj.draw.contracts;

import aguiaj.draw.IDimension;

public class DimensionUtil {

	public static boolean isValidPoint(IDimension dim, int x, int y) {
		return x >= 0 && x < dim.getWidth() && y >= 0 && y < dim.getHeight();
	}
}
