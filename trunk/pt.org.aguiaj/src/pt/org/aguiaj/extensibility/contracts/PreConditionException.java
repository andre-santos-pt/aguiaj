package pt.org.aguiaj.extensibility.contracts;

public class PreConditionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PreConditionException(String message) {
		super(message);
	}
}
