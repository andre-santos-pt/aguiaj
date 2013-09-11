package aguiaj.draw;


public interface Grid  {

	Dimension getDimension();

	int getPositionWidth();
	int getPositionHeight();
	
	RGBColor getBackground(int row, int column);
	
	Image getImageAt(int row, int column);
}
