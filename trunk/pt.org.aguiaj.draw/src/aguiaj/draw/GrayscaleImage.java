package aguiaj.draw;

import aguiaj.draw.Image;

public class GrayscaleImage implements Image {
		
	private int[][] pixels;
	private ConstantDimension dimension;
	
	public GrayscaleImage(int width, int height) {
		this(new int[height][width]);
	}
	
	private GrayscaleImage(int[][] pixels) {
		this.pixels = pixels;
		dimension = new ConstantDimension(pixels[0].length, pixels.length);
	}
	
//	@Override
//	public int getWidth() {
//		return pixels[0].length;
//	}
//
//	@Override
//	public int getHeight() {
//		return pixels.length;
//	}

	@Override
	public GrayTone getColor(int x, int y) {
		return new GrayTone(pixels[y][x]);
	}
	
	
	
	public void invert() {
		for(int y = 0; y < pixels.length; y++)
			for(int x = 0; x < pixels[y].length; x++)
				pixels[y][x] = 255 - pixels[y][x];
	}

	@Override
	public GrayscaleImage copy() {
		int[][] copy = new int[pixels.length][pixels[0].length];
		for(int y = 0; y < pixels.length; y++)
			for(int x = 0; x < pixels[y].length; x++)
				copy[y][x] = pixels[y][x];
		
		return new GrayscaleImage(copy);
	}

	@Override
	public Dimension getDimension() {
		return dimension;
	}
}
