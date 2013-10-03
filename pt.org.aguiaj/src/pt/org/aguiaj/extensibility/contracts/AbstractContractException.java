package pt.org.aguiaj.extensibility.contracts;

public abstract class AbstractContractException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AbstractContractException(Class<?> clazz, String operation, String message) {
		super(clazz.getSimpleName() + "." + operation + "(...) : " + message);
	}
}
