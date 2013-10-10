package aguiaj.draw;

/**
 * Represents a rectangular dimension in terms of width and height.
 * 
 * @author Andre L. Santos
 */
public interface IDimension {

	/**
	 * Dimension width.
	 * @return a value greater or equal to zero.
	 */
	int getWidth();
	
	/**
	 * Dimension height.
	 * @return a value greater or equal to zero.
	 */
	int getHeight();

}
