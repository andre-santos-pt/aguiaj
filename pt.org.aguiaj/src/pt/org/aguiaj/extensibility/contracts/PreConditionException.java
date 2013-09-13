package pt.org.aguiaj.extensibility.contracts;

public class PreConditionException extends RuntimeException implements ContractException {

	private static final long serialVersionUID = 1L;

	public PreConditionException(String message) {
		super(message);
	}
}
