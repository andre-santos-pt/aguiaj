package aguiaj.draw;


public interface IGrid  {

	IDimension getDimension();

	int getPositionWidth();
	int getPositionHeight();
	
	IColor getBackground(int row, int column);
	
	IImage getImageAt(int row, int column);
}
