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

public class RectangleFill extends DrawItem {
	final Color color;
	final int x1;
	final int y1;
	
	public RectangleFill(int x0, int y0, int x1, int y1, Color color) {
		super(x0, y0);
		this.color = color;
		this.x1 = x1;
		this.y1 = y1;
	}


	@Override
	public void draw(GC gc) {
		gc.setBackground(color);
		gc.fillRectangle(x, y, x1, y1);
	}
}
