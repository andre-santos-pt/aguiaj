package aguiaj.draw.contracts;

import aguiaj.draw.RGBColor;
import aguiaj.draw.Dimension;
import aguiaj.draw.Image;
import aguiaj.draw.TransparentImage;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;

public class TransparentImageContract implements TransparentImage, ContractDecorator<TransparentImage>{

	private final TransparentImage image;
	
	
	public TransparentImageContract(TransparentImage image) {
		this.image = image;
	}
	
	@Override
	public TransparentImage getWrappedObject() {
		return image;
	}

//	@Override
//	public int getWidth() {
//		return image.getWidth();
//	}
//
//	@Override
//	public int getHeight() {
//		return image.getWidth();
//	}
	
	@Override
	public Dimension getDimension() {
		return image.getDimension();
	}

	@Override
	public RGBColor getColor(int x, int y) {
		return image.getColor(x, y);
	}

	@Override
	public int getOpacity(int x, int y) {
		Dimension dim = getDimension();
		
		int w = dim.getWidth();
		int h = dim.getHeight();
		
		if(x < 0 || x >= w)
			throw new PreConditionException("Invalid x coordinate: " + x + " (valid range [0, " + (w-1) + "])");
		
		if(y < 0 || y >= h)
			throw new PreConditionException("Invalid y coordinate: " + y + " (valid range [0, " + (h-1) + "])");
		
		
		int op = image.getOpacity(x, y);
		validateOpacity(op);
		return op;
	}
	
	@Override
	public void checkInvariant() throws InvariantException {
		Dimension dim = getDimension();
		int w = dim.getWidth();
		int h = dim.getHeight();
		
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				validateOpacity(image.getOpacity(x, y));
	}

	private static void validateOpacity(int op) {
		if(op < 0 || op > 100)
			throw new PostConditionException(op + " : Opacity value must be within [0, 100]");
	}
	
}
