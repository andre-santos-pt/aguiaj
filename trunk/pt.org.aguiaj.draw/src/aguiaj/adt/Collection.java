package aguiaj.adt;

/**
 * Represents a collection of elements.
 * 
 * @author Andre L. Santos
 */
public interface Collection {

	/**
	 * Is the collection empty?
	 * @return true if yes, false otherwise
	 */
	boolean isEmpty();
	
	/**
	 * Returns the number of elements in the collection.
	 * @return an integer greater or equal to zero
	 */
	int getSize();
}
