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
import org.eclipse.swt.graphics.Point;

public class LineDraw extends DrawItem {
	final Point destiny;
	public LineDraw(Point origin, Point destiny) {
		super(origin.x, origin.y);
		this.destiny = destiny;
	}
	
	@Override
	public void draw(GC gc) {
		gc.drawLine(x, y, destiny.x, destiny.y);
	}

}
