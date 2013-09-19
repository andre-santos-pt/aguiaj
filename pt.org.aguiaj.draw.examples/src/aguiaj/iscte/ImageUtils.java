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

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.extensibility.canvas.ImageDraw;
import aguiaj.draw.IColor;
import aguiaj.draw.IDimension;
import aguiaj.draw.IImage;
import aguiaj.draw.ITransparentImage;



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
	
//	public static ColorImage fromSwtImage(org.eclipse.swt.graphics.Image image) {
//		if(image == null)
//			throw new NullPointerException("Image cannot be null");
//		
//		ColorImage img = ColorImage.fromMatrix(fetchData(image));
//		int[][] alphaData = fetchAlpha(image);
//
//		if(alphaData != null)
//			img.setOpacity(alphaData);
//
//		return img;
//	}

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
	
 
 static ImageDraw createImageDraw(IImage image, Point origin, int zoom) {
		PaletteData palette = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
		IDimension dim = image.getDimension();
		int width = dim.getWidth();
		int height = dim.getHeight();
		ImageData data = new ImageData(width, height, 24, palette);
		data.alpha = -1;
		byte[] alpha = new byte[width*height];
		int[] v = new int[width*height];
		
		int i = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				aguiaj.draw.IColor pixel = image.getColor(x, y);
				v[i] = palette.getPixel(new RGB(pixel.getR(), pixel.getG(), pixel.getB()));
				if(image instanceof ITransparentImage) {
					int t = ((ITransparentImage)image).getOpacity(x, y);
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
		
		return new ImageDraw(data, origin);
	}
}
