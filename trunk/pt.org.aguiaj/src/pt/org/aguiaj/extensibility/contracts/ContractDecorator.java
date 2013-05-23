package pt.org.aguiaj.extensibility.contracts;

public interface ContractDecorator<T> {

	final static String CHECK_INVARIANT = "checkInvariant";
	
	void checkInvariant() throws InvariantException;

	T getWrappedObject();
}
