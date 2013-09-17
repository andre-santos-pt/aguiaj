package aguiaj.draw.contracts;

import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;
import aguiaj.draw.IDimension;

public class DimensionContract implements IDimension, ContractDecorator<IDimension> {

	private final IDimension dimension;
	
	public DimensionContract(IDimension dimension) {
		if(dimension == null)
			throw new NullPointerException("argument cannot be null");
		
		this.dimension = dimension;
	}
	

	@Override
	public int getWidth() {
		int w = dimension.getWidth();
		if(w < 0)
			throw new PostConditionException("height must be greater or equal to zero");
		
		return w;
	}

	@Override
	public int getHeight() {
		int h = dimension.getHeight();
		if(h < 0)
			throw new PostConditionException("height must be greater or equal to zero");
		return h;
	}

	@Override
	public boolean isValidPoint(int x, int y) {
		if(x < 0 || x >= getWidth())
			throw new PreConditionException("invalid x: " + x);
		
		if(y < 0 || y >= getHeight())
			throw new PreConditionException("invalid y: " + y);
		
		return dimension.isValidPoint(x, y);
	}

	@Override
	public void checkInvariant() throws InvariantException {
		
	}

	@Override
	public IDimension getWrappedObject() {
		return dimension;
	}
	
}
