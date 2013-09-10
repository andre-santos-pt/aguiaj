package aguiaj.adt;


/**
 * Represents a stack.
 * 
 * @author Andre L. Santos
 */
public interface Stack extends Collection {
	
	/**
	 * Removes an element from the top of the stack.
	 * @return a non-null reference to the removed element
	 */
	Object pop();
	
	/**
	 * Inserts an element on the top of the stack.
	 * @param object a reference to the object to be inserted. Null references are not allowed.
	 */
	void push(Object object);
}
