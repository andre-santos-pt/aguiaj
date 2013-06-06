package aguiaj.draw;


public interface Grid  {

	int getNumberOfRows();
	int getNumberOfColumns();

	int getPositionWidth();
	int getPositionHeight();
	
	Color getBackground(int row, int column);
	
	Image getImage(int row, int column);
}