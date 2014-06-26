package aguiaj.images;

import aguiaj.colors.Color;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;

public class ImageContract implements Image, ContractDecorator<Image>{

	private final Image image;
	
	private int constWidth = -1;
	private int constHeight = -1;
	
	public ImageContract(Image image) {
		this.image = image;
	}
	
	@Override
	public Image getWrappedObject() {
		return image;
	}

	@Override
	public int getWidth() {

		int w = image.getWidth();
		if(w < 1)
			throw new PostConditionException(Image.class, "getWidth", "Width must be positive");
		
		if(constWidth == -1)
			constWidth = w;
		
		return w;
	}

	@Override
	public int getHeight() {
		int h = image.getWidth();
		if(h < 1)
			throw new PostConditionException(Image.class, "getHeight", "Height must be positive");
		
		if(constHeight == -1)
			constHeight = h;
		
		return h;
	}

	@Override
	public Color getColor(int x, int y) {
		if(x < 0 || x >= getWidth())
			throw new PreConditionException(Image.class, "getColor", "Invalid x coordinate: " + x + " (valid range [0, " + (getWidth()-1) + "])");
		
		if(y < 0 || y >= getHeight())
			throw new PreConditionException(Image.class, "getColor", "Invalid y coordinate: " + y + " (valid range [0, " + (getHeight()-1) + "])");
		
		
		Color c = image.getColor(x, y);
		if(c == null)
			throw new PostConditionException(Image.class, "getColor", "The color of a pixel cannot be null");
		
		return c;
	}

	@Override
	public void checkInvariant() throws InvariantException {
		if(constWidth != -1 && constWidth != getWidth())
			throw new InvariantException("Image width must be constant");
	
		if(constHeight != -1 && constHeight != getHeight())
			throw new InvariantException("Image height must be constant");
	}
}
