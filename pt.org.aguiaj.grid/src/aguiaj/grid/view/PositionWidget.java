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
package aguiaj.grid.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.extensibility.CanvasVisualizationWidgetAdapter;
import aguiaj.colors.Color;
import aguiaj.grid.Position;
import aguiaj.images.Image;
import aguiaj.images.contribution.Common;

public class PositionWidget 
extends CanvasVisualizationWidgetAdapter<Position> {
	
	private static final int PIXELS = 36;
	
	private Position position;
	private int width;
	private int height;

	
	@Override
	public void update(Position position) {
		this.position = position;
		if(position != null) {
			width = PIXELS + 1;
			height = PIXELS + 1;
		}
	}

	@Override
	public int canvasHeight() {
		return height;
	}

	@Override
	public int canvasWidth() {
		return width;
	}

	@Override
	public void drawObject(GC gc) {
		assert position != null;
		
		Display display = Display.getDefault();
		
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
		
		for(int x = 0; x < width; x += PIXELS)
			gc.drawLine(x, 0, x, height);
		
		for(int y = 0; y < height; y += PIXELS)
			gc.drawLine(0, y, width, y);
		
		Color bgcolor = position.getBackground();
		RGB rgb = new RGB(bgcolor.getR(), bgcolor.getG(), bgcolor.getB());
		gc.setBackground(new org.eclipse.swt.graphics.Color(display, rgb));
		gc.fillRectangle(1, 1, PIXELS-1, PIXELS-1);
		
		Image icon = position.getIcon();
		if(icon != null)
			Common.drawImage(icon, gc, 3, 3, 1);
		
		if(bgcolor.getLuminance() < 128)
			gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		
		gc.drawText("(" + position.getRow() + ", " + position.getColumn() + ")", 1, 1);
	}	
}
