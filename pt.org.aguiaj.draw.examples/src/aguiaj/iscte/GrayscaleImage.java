package aguiaj.iscte;


import aguiaj.draw.IImage;

public class GrayscaleImage implements IImage {
		
	
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
	public Color getColor(int x, int y) {
		validatePoint(x, y);
		return Color.createGraytone(pixels[y][x]);
	}
	
	public int getWidth() {
		return dimension.getWidth();
	}
	
	public int getHeight() {
		return dimension.getHeight();
	}
	
	public int getGraytone(int x, int y) {
		return pixels[y][x];
	}
	
	public void setGraytone(int x, int y, int tone) {
		validatePoint(x, y);
		pixels[y][x] = tone;
	}
	
	public void invert() {
		for(int y = 0; y < pixels.length; y++)
			for(int x = 0; x < pixels[y].length; x++)
				pixels[y][x] = 255 - pixels[y][x];
	}

	private void validatePoint(int x, int y) {
		if(!dimension.isValidPoint(x, y))
			throw new IllegalArgumentException("Invalid point - (" + x + ", " + y + ")");
	}


}
