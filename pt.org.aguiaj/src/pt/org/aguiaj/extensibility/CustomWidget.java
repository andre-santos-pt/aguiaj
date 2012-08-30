package pt.org.aguiaj.extensibility;

/**
 * Represents a custom widget that can be plugged into the AGUIA/J framework.
 *
 * @param <T> The type of domain object that this widget renders.
 */
public interface CustomWidget<T> {

	 /**
	  * Update the widget given the domain object.
	  * 
	  * @param object Object to update. Contract: will never be passed null
	  */
	void update(T object);
	
}
