package aguiaj.draw.contracts;

import aguiaj.draw.Color;
import aguiaj.draw.Dimension;
import aguiaj.draw.Grid;
import aguiaj.draw.Image;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;

public final class GridContract implements Grid, ContractDecorator<Grid> {

	private final Grid grid;
	
	public GridContract(Grid grid) {
		if(grid == null)
			throw new NullPointerException("wrapped object cannot be null");
		
		this.grid = grid;
	}
	
	@Override
	public Grid getWrappedObject() {
		return grid;
	}
	
	private void checkRowColumn(int row, int column) {
		if(row < 0 || row >= getNumberOfRows())
			throw new PreConditionException("Invalid row: " + row);
		
		if(column < 0 || column >= getNumberOfColumns())
			throw new PreConditionException("Invalid column: " + column);
	}
	
	
	private int constRows = -1;

	@Override
	public int getNumberOfRows() {
		int n = grid.getNumberOfRows();
		
		if(n < 1)
			throw new PostConditionException("Number of rows must be positive");
	
		if(constRows != -1 && n != constRows)
			throw new PostConditionException("number of rows must be constant");
		
		if(constRows == -1)
			constRows = n;
		
		return n;
	}

	
	private int constCols = -1;
	
	@Override
	public int getNumberOfColumns() {
		int n = grid.getNumberOfColumns();
		if(n < 1)
			throw new PostConditionException("Number of columns must be positive");
		
		if(constCols != -1 && n != constCols)
			throw new PostConditionException("number of columns must be constant");
		
		if(constCols == -1)
			constCols = n;
		
		return n;
	}
	
	@Override
	public Color getBackground(int row, int column) {
		checkRowColumn(row, column);
		
		Color color = grid.getBackground(row, column);
		if(color == null)
			throw new PostConditionException("Background cannot be null");
		
		return color;
	}

	

	@Override
	public Image getImage(int row, int column) {
		checkRowColumn(row, column);
		
		return grid.getImage(row, column);
	}

	@Override
	public void checkInvariant() {
		if(constRows != -1 && constRows != getNumberOfRows())
			throw new InvariantException("number of rows should be constant");
	
		if(constCols != -1 && constCols != getNumberOfColumns())
			throw new PostConditionException("number of columns must be constant");
	}

	@Override
	public int getPositionWidth() {
		return grid.getPositionWidth();
	}

	@Override
	public int getPositionHeight() {
		return grid.getPositionHeight();
	}

	@Override
	public Dimension getDimension() {
		return grid.getDimension();
	}

	

}
