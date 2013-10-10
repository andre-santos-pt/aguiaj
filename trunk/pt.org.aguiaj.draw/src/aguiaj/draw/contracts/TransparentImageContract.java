package aguiaj.draw.contracts;

import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;
import aguiaj.draw.IColor;
import aguiaj.draw.IDimension;
import aguiaj.draw.ITransparentImage;

public class TransparentImageContract implements ITransparentImage, ContractDecorator<ITransparentImage>{

	private final ITransparentImage image;
	
	private final ImageContract imageContract;
	
	public TransparentImageContract(ITransparentImage image) {
		this.image = image;
		imageContract = new ImageContract(image);
	}
	
	@Override
	public ITransparentImage getWrappedObject() {
		return image;
	}

	@Override
	public IDimension getDimension() {
		return imageContract.getDimension();
	}

	@Override
	public IColor getColor(int x, int y) {
		return image.getColor(x, y);
	}

	@Override
	public int getOpacity(int x, int y) {
		IDimension dim = getDimension();
		
		int w = dim.getWidth();
		int h = dim.getHeight();
		
		if(x < 0 || x >= w)
			throw new PreConditionException(image.getClass(), "getOpacity", "Invalid x coordinate: " + x + " (valid range [0, " + (w-1) + "])");
		
		if(y < 0 || y >= h)
			throw new PreConditionException(image.getClass(), "getOpacity", "Invalid y coordinate: " + y + " (valid range [0, " + (h-1) + "])");
		
		
		int op = image.getOpacity(x, y);
		validateOpacity(op);
		return op;
	}
	
	private void validateOpacity(int op) {
		if(op < 0 || op > 100)
			throw new PostConditionException(image.getClass(), "getOpacity", "Invalid value " + op + ", must be within [0, 100]");
	}
	
	
	@Override
	public void checkInvariant() throws InvariantException {
		IDimension dim = getDimension();
		int w = dim.getWidth();
		int h = dim.getHeight();
		
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				validateOpacity(image.getOpacity(x, y));
	}

	
	
}
