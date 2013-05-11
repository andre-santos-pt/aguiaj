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
package aguiaj.images.contribution;


import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import aguiaj.colors.Color;
import aguiaj.images.Image;
import aguiaj.images.ImageWithTransparency;

public class Common {

	public static void drawImage(Image image, GC gc, int destX, int destY, int zoom) {
		drawImage(image, gc, destX, destY, Integer.MAX_VALUE, Integer.MAX_VALUE, zoom);
	}
	
	public static void drawImage(Image image, GC gc, int destX, int destY, int maxWidth, int maxHeight, int zoom) {
		PaletteData palette = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
		int height = Math.min(image.getHeight(), maxHeight);
		int width = Math.min(image.getWidth(), maxWidth);
		ImageData data = new ImageData(width, height, 24, palette);
		data.alpha = -1;
		byte[] alpha = new byte[width*height];
		int[] v = new int[width*height];
		
		int i = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color pixel = image.getColor(x, y);
				v[i] = palette.getPixel(new RGB(pixel.getR(), pixel.getG(), pixel.getB()));
				if(image instanceof ImageWithTransparency) {
					int t = ((ImageWithTransparency)image).getOpacity(x, y);
					alpha[i] = (byte) ((t*255)/100);
				}
				else {
					alpha[i] = (byte) 255;
				}
				i++;
			}
		}

		data.setPixels(0, 0, v.length, v, 0); 
		data.setAlphas(0, 0, alpha.length, alpha, 0); 
		
		data = data.scaledTo(width*zoom, height*zoom);
		
		gc.drawImage(new org.eclipse.swt.graphics.Image(Display.getDefault(), data), destX*zoom, destY*zoom);
	}
}
