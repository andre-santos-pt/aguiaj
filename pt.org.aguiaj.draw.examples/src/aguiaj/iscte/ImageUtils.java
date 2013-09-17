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
package aguiaj.iscte;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import aguiaj.draw.IColor;
import aguiaj.draw.IDimension;
import aguiaj.draw.IImage;



/**
 * Static class with utility methods for creating images.
 *
 * @author Andre L. Santos
 */
public class ImageUtils {
	static final int R = 0;
	static final int G = 1;
	static final int B = 2;
	
	public static ColorImage loadColorImage(String filePath) {
		int[][][] data = fetchData(new org.eclipse.swt.graphics.Image(Display.getDefault(), filePath));
		return ColorImage.fromMatrix(data);
	}

	public static BinaryImage loadBinaryImage(String filePath) {
		return convertToBinary(loadColorImage(filePath));
	}
	
	public static GrayscaleImage loadBlackWhiteImage(String filePath) {
		return convertToBlackWhite(loadColorImage(filePath));
	}
	
	public static ColorImage fromSwtImage(org.eclipse.swt.graphics.Image image) {
		if(image == null)
			throw new NullPointerException("Image cannot be null");
		
		ColorImage img = ColorImage.fromMatrix(fetchData(image));
		int[][] alphaData = fetchAlpha(image);

		if(alphaData != null)
			img.setOpacity(alphaData);

		return img;
	}

	private static int getLuminance(IColor color) {
		return (int) Math.round(0.3*color.getR() + 0.59*color.getG() + 0.11*color.getB());
	}
	
	private static BinaryImage convertToBinary(IImage image) {
		IDimension dim = image.getDimension();
		BinaryImage img = new BinaryImage(dim.getWidth(), dim.getHeight());
		for(int i = 0; i < dim.getWidth(); i++) {
			for(int j = 0; j < dim.getHeight(); j++) {
				if(getLuminance(image.getColor(i, j)) < 128)
					img.setBlack(i, j);
			}
		}
		return img;
	}
	
	private static GrayscaleImage convertToBlackWhite(IImage image) {
		IDimension dim = image.getDimension();
		GrayscaleImage img = new GrayscaleImage(dim.getWidth(), dim.getHeight());
		for(int i = 0; i < dim.getWidth(); i++) {
			for(int j = 0; j < dim.getHeight(); j++) {
				img.setGraytone(i, j, getLuminance(image.getColor(i, j)));
			}
		}
		return img;
	}
	
	private static int[][][] fetchData(org.eclipse.swt.graphics.Image image) {
		ImageData data = image.getImageData();
		PaletteData palette = data.palette;
		int[][][] pixels = new int[data.height][data.width][3];

		for(int y = 0; y < data.height; y++) {
			for(int x = 0; x < data.width; x++) {
				int p = data.getPixel(x, y);
				RGB rgb = palette.getRGB(p);
				pixels[y][x][R] = rgb.red;
				pixels[y][x][G] = rgb.green;
				pixels[y][x][B] = rgb.blue;
			}
		}
		return pixels;
	}

	private static int[][] fetchAlpha(org.eclipse.swt.graphics.Image image) {
		ImageData data = image.getImageData();

		int[][] alpha = new int[data.height][data.width];
		int a = 0;
		for(int y = 0; y < data.height; y++)
			for(int x = 0; x < data.width; x++) {
				if(data.alphaData != null)
					alpha[y][x] = (((int)data.alphaData[a] & 0xff)*100)/255;
				else if(data.transparentPixel != -1 && data.transparentPixel != data.getPixel(x, y))
					alpha[y][x] = 100;

				a++;
			}

		return alpha;
	}
	

	static Color[][] setAll(Color[][] data, Color color) {
		for(Color[] row : data)
			for(int i = 0; i < row.length; i++)
				row[i] = color;

		return data;
	}

}
