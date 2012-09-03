package aguiaj.images;

/**
 * Represents an image with transparency.
 */
public interface ImageWithTransparency extends Image {

	/**
	 * The opacity level at an image point (100 - fully opaque; 0 - transparent).
	 * 
	 * @param x x-axis coordinate
	 * @param y y-axis coordinate
	 * @return Opacity (range: 0-100)
	 */
	int getOpacity(int x, int y);
}
