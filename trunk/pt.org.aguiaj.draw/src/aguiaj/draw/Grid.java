package aguiaj.draw;


public interface Grid extends TwoDimensional {

	int getNumberOfRows();
	int getNumberOfColumns();

	int getPositionWidth();
	int getPositionHeight();
	
	RGBColor getBackground(int row, int column);
	
	Image getImage(int row, int column);
}
