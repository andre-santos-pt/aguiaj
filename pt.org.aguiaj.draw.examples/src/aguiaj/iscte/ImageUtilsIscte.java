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
package aguiaj.iscte;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.swt.graphics.Image;
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
public class ImageUtilsIscte {
	private static final int R = 0;
	private static final int G = 1;
	private static final int B = 2;

	public static ColorImage loadColorImage(String filePath) {
		if(!new File(filePath).exists())
			throw new RuntimeException("file not found: " + filePath);
		
		Image image = new Image(Display.getDefault(), filePath);

		int[][][] data = fetchData(image);
		ColorImage colorImage = ColorImage.fromMatrix(data);

		int[][] alpha = fetchAlpha(image);
		if(!allZeros(alpha))
			colorImage.setOpacity(alpha);

		return colorImage;
	}

	private static boolean allZeros(int[][] alpha) {
		for(int i = 0; i < alpha.length; i++)
			for(int j = 0; j < alpha[i].length; j++)
				if(alpha[i][j] != 0)
					return false;

		return true;
	}

	public static BinaryImage loadBinaryImage(String filePath) {
		return convertToBinary(loadColorImage(filePath));
	}

	public static GrayscaleImage loadGrayscaleImage(String filePath) {
		return convertToGrayscale(loadColorImage(filePath));
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

	private static GrayscaleImage convertToGrayscale(IImage image) {
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

	private static int[][] fetchAlpha(Image image) {
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

}
