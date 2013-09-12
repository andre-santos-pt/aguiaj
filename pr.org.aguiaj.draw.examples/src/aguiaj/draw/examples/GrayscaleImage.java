package aguiaj.draw.examples;


import aguiaj.draw.Dimension;
import aguiaj.draw.Image;

public class GrayscaleImage implements Image {
		
	
	private final int[][] pixels;
	private final Dimension dimension;
	
	public GrayscaleImage(int width, int height) {
		this(new int[height][width]);
	}
	
	private GrayscaleImage(int[][] pixels) {
		this.pixels = pixels;
		dimension = new Dimension(pixels[0].length, pixels.length);
	}
	
	public static GrayscaleImage create(int[][] pixels) {
		if(!validMatrix(pixels))
			throw new IllegalArgumentException("invalid matrix for grayscale image");
		
		int[][] copy = new int[pixels.length][pixels[0].length];
		for(int y = 0; y < pixels.length; y++)
			for(int x = 0; x < pixels[y].length; x++)
				copy[y][x] = pixels[y][x];
		
		return new GrayscaleImage(copy);
	}

	private static boolean validMatrix(int[][] pixels) {
		// ...
		return true;
	}
	
	@Override
	public Dimension getDimension() {
		return dimension;
	}
	
	@Override
	public Graytone getColor(int x, int y) {
		return Graytone.get(pixels[y][x]);
	}
	

	
	public void invert() {
		for(int y = 0; y < pixels.length; y++)
			for(int x = 0; x < pixels[y].length; x++)
				pixels[y][x] = 255 - pixels[y][x];
	}




}
