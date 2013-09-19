package aguiaj.draw.contracts;

import aguiaj.draw.IDimension;
import aguiaj.draw.IColor;
import aguiaj.draw.IImage;
import aguiaj.draw.ITransparentImage;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;

public class ImageContract implements IImage, ContractDecorator<IImage>{

	private final IImage image;
	
	private int constWidth = -1;
	private int constHeight = -1;
	
	public ImageContract(IImage image) {
		this.image = image;
	}
	
	@Override
	public IImage getWrappedObject() {
		return image;
	}

	@Override
	public IDimension getDimension() {
		IDimension dim = image.getDimension();
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
	public IColor getColor(int x, int y) {
		IDimension dim = getDimension();
		
		if(!dim.isValidPoint(x, y))
			throw new PreConditionException("Invalid coordinate (" + x + ", " + y + ")");
		
//		if(x < 0 || x >= getWidth())
//			throw new PreConditionException("Invalid x coordinate: " + x + " (valid range [0, " + (getWidth()-1) + "])");
//		
//		if(y < 0 || y >= getHeight())
//			throw new PreConditionException("Invalid y coordinate: " + y + " (valid range [0, " + (getHeight()-1) + "])");
		
		
		IColor color = image.getColor(x, y);
		if(color == null)
			throw new PostConditionException("The color of a pixel cannot be null - (" + x + ", " + y + ")");
		
//		RGBColorContract.validate(color);
		
		return color;
	}

	@Override
	public void checkInvariant() throws InvariantException {
	
		IDimension dim = getDimension();
		
		int w = dim.getWidth();
		validateWidth(w);
		int h = dim.getHeight();
		validateHeight(h);
		
//		for(int y = 0; y < h; y++)
//			for(int x = 0; x < w; x++)
//				RGBColorContract.validate(image.getColor(x, y));
		
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
