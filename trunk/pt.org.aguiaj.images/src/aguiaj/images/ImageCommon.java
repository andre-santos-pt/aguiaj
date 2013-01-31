package aguiaj.images;

class ImageCommon {

	static boolean isValidPoint(int x, int y, Image image) {
		return y >= 0 && y < image.getHeight() && x >= 0 && x < image.getWidth(); 
	}

	static boolean isValidDimension(int width, int height) {
		return width > 0 && height > 0;
	}
}
