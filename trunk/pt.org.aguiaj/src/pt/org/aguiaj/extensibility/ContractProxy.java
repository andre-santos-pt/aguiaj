package pt.org.aguiaj.extensibility;

public interface ContractProxy<T> {

	final static String CHECK_INVARIANT = "checkInvariant";
	
	void checkInvariant() throws InvariantException;
	
	T getProxiedObject();
}
