package pt.org.aguiaj.extensibility.contracts;

public class InvariantException extends RuntimeException implements ContractException {

	private static final long serialVersionUID = 1L;

	public InvariantException(String message) {
		super(message);
	}
}
