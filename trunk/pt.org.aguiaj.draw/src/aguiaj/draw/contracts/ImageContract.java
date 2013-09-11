package aguiaj.draw.contracts;

import aguiaj.draw.RGBColor;
import aguiaj.draw.Dimension;
import aguiaj.draw.Image;
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
	public Dimension getDimension() {
		Dimension dim = image.getDimension();
		if(dim == null)
			throw new PostConditionException("Dimension cannot be null");
		
		int w = dim.getWidth();
		validateWidth(w);
		
		if(constWidth == -1)
			constWidth = w;
		
		int h = dim.getHeight();
		validateHeight(h);
		
		if(constHeight == -1)
			constHeight = h;
		
		return dim;
	}
	
//	@Override
//	public int getWidth() {
//		int w = image.getWidth();
//		validateWidth(w);
//		
//		if(constWidth == -1)
//			constWidth = w;
//		
//		return w;
//	}
//
//	@Override
//	public int getHeight() {
//		int h = image.getWidth();
//		validateHeight(h);
//		
//		if(constHeight == -1)
//			constHeight = h;
//		
//		return h;
//	}

	@Override
	public RGBColor getColor(int x, int y) {
		Dimension dim = getDimension();
		
		if(!dim.isValidPoint(x, y))
			throw new PreConditionException("Invalid coordinate (" + x + ", " + y + ")");
		
//		if(x < 0 || x >= getWidth())
//			throw new PreConditionException("Invalid x coordinate: " + x + " (valid range [0, " + (getWidth()-1) + "])");
//		
//		if(y < 0 || y >= getHeight())
//			throw new PreConditionException("Invalid y coordinate: " + y + " (valid range [0, " + (getHeight()-1) + "])");
		
		
		RGBColor color = image.getColor(x, y);
		if(color == null)
			throw new PostConditionException("The color of a pixel cannot be null");
		
		ColorContract.validate(color);
		
		return color;
	}

	@Override
	public void checkInvariant() throws InvariantException {
	
		Dimension dim = getDimension();
		
		int w = dim.getWidth();
		validateWidth(w);
		int h = dim.getHeight();
		validateHeight(h);
		
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				ColorContract.validate(image.getColor(x, y));
		
		if(constWidth != -1 && constWidth != w)
			throw new InvariantException("Image width must be constant");
	
		if(constHeight != -1 && constHeight != h)
			throw new PostConditionException("Image height must be constant");
	}

	private static void validateWidth(int w) {
		if(w < 1)
			throw new PostConditionException("Width must be positive");
	}

	private static void validateHeight(int h) {
		if(h < 1)
			throw new PostConditionException("Height must be positive");
	}

}
