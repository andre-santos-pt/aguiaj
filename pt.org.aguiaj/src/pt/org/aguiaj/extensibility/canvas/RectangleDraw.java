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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class RectangleDraw extends DrawItem {
	final Color color;
	final int width;
	final int height;
	
	public RectangleDraw(int x0, int y0, int width, int height, Color color) {
		super(x0, y0);
		this.color = color;
		this.width = width;
		this.height = height;
	}


	@Override
	public void draw(GC gc) {
		gc.setForeground(color);
		gc.drawRectangle(x, y, width, height);
	}
}
