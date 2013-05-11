package aguiaj.images;

import aguiaj.colors.Color;
import pt.org.aguiaj.extensibility.ContractProxy;
import pt.org.aguiaj.extensibility.InvariantException;
import pt.org.aguiaj.extensibility.PostConditionException;
import pt.org.aguiaj.extensibility.PreConditionException;

public class ImageContract implements Image, ContractProxy<Image>{

	private final Image image;
	
	private int constWidth = -1;
	private int constHeight = -1;
	
	public ImageContract(Image image) {
		this.image = image;
	}
	
	@Override
	public Image getProxiedObject() {
		return image;
	}

	@Override
	public int getWidth() {

		int w = image.getWidth();
		if(w < 1)
			throw new PostConditionException("Width must be positive");
		
		if(constWidth == -1)
			constWidth = w;
		
		return w;
	}

	@Override
	public int getHeight() {
		int h = image.getWidth();
		if(h < 1)
			throw new PostConditionException("Height must be positive");
		
		if(constHeight == -1)
			constHeight = h;
		
		return h;
	}

	@Override
	public Color getColor(int x, int y) {
		if(x < 0 || x >= getWidth())
			throw new PreConditionException("Invalid x coordinate: " + x + " (valid range [0, " + (getWidth()-1) + "])");
		
		if(y < 0 || y >= getHeight())
			throw new PreConditionException("Invalid y coordinate: " + y + " (valid range [0, " + (getHeight()-1) + "])");
		
		
		Color c = image.getColor(x, y);
		if(c == null)
			throw new PostConditionException("The color of a pixel cannot be null");
		
		return c;
	}

	@Override
	public void checkInvariant() throws InvariantException {
		if(constWidth != -1 && constWidth != getWidth())
			throw new InvariantException("Image width must be constant");
	
		if(constHeight != -1 && constHeight != getHeight())
			throw new PostConditionException("Image height must be constant");
	}
}
