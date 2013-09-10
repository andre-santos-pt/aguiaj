package aguiaj.draw;

public class Dimension implements Comparable<Dimension>{

	private final int width;
	private final int height;
	
	public Dimension(int width, int height) {
		if(!isValidDimension(width, height))
			throw new IllegalArgumentException("width and height must be greater than zero");
		
		this.width = width;
		this.height = height;
	}
	
	public static boolean isValidDimension(int width, int height) {
		return width > 0 && height > 0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getArea() {
		return width * height;
	}
	
	public boolean isValidPoint(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	public Dimension scale(double factor) {
		if(factor <= 0.0)
			throw new IllegalArgumentException("factor must be greater than zero");
		
		return new Dimension((int) Math.round(width * factor), (int) Math.round(height * factor));
	}
	
	
	@Override
	public String toString() {
		return width + " x " + height;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;
		
		Dimension other = (Dimension) obj;
		return height == other.height && width == other.width;
	}

	@Override
	public int compareTo(Dimension d) {
		if(d == null)
			throw new NullPointerException("argument cannot be null");
		
		return new Integer(getArea()).compareTo(d.getArea());
	}
}
