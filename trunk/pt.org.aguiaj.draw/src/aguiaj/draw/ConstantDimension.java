package aguiaj.draw;

public class ConstantDimension implements Dimension {

	private final int width;
	private final int height;
	
	public ConstantDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isValidPoint(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	@Override
	public String toString() {
		return width + " x " + height;
	}

}
