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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class ImageDraw extends DrawItem {
	final ImageData data;
	
	public ImageDraw(ImageData data, Point location) {
		super(location.x, location.y);
		this.data = data;
	}

	@Override
	public void draw(GC gc) {
		Image img = new Image(gc.getDevice(), data);
		gc.drawImage(img, x, y);
		img.dispose();
	}	
}
