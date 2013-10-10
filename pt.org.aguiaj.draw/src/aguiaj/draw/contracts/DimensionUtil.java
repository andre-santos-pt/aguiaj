package aguiaj.draw.contracts;

import aguiaj.draw.IDimension;

public class DimensionUtil {

	public static boolean isValidPoint(IDimension dim, int x, int y) {
		return x >= 0 && x < dim.getWidth() && y >= 0 && y < dim.getHeight();
	}
}
