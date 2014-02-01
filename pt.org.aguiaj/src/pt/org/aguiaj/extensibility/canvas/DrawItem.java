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
package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.graphics.GC;

public abstract class DrawItem {
	protected final int x;
	protected final int y;

	protected DrawItem(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract void draw(GC gc);
}
