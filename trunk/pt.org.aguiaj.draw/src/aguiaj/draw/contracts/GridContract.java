package aguiaj.draw.contracts;

import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;
import aguiaj.draw.IColor;
import aguiaj.draw.IDimension;
import aguiaj.draw.IGrid;
import aguiaj.draw.IImage;

public final class GridContract implements IGrid, ContractDecorator<IGrid> {

	private final IGrid grid;
	
	private IDimension constDimension;
	
	public GridContract(IGrid grid) {
		if(grid == null)
			throw new NullPointerException("wrapped object cannot be null");
		
		this.grid = grid;
	}
	
	@Override
	public IGrid getWrappedObject() {
		return grid;
	}
	
	private void checkRowColumn(int row, int column, String operation) {
		if(!DimensionUtil.isValidPoint(getDimension(), column, row))
			throw new PreConditionException(grid.getClass(), operation, "invalid point " + row + ", " + column);
	}
	
	
	@Override
	public IColor getBackground(int row, int column) {
		checkRowColumn(row, column, "getBackground");
		
		IColor color = grid.getBackground(row, column);
		if(color == null)
			throw new PostConditionException(grid.getClass(), "getBackground", "Background cannot be null");
		
		return color;
	}

	

	@Override
	public IImage getImageAt(int row, int column) {
		checkRowColumn(row, column, "getImageAt");
		
		return grid.getImageAt(row, column);
	}

	@Override
	public void checkInvariant() {
//		if(constRows != -1 && constRows != getNumberOfRows())
//			throw new InvariantException("number of rows should be constant");
//	
//		if(constCols != -1 && constCols != getNumberOfColumns())
//			throw new PostConditionException("number of columns must be constant");
		getDimension();
	}

	@Override
	public int getPositionPixels() {
		int p = grid.getPositionPixels();
		if(p < 1)
			throw new PostConditionException(grid.getClass(), "getPositionPixels", "number of position side pixels must be positive");
		
		return p;
	}

	
	@Override
	public IDimension getDimension() {
		IDimension d = grid.getDimension();
		if(constDimension != null && !constDimension.equals(d))
			throw new InvariantException("Grid dimension must be constant");
		else
			constDimension = d;
		
		return d;
	}

}
