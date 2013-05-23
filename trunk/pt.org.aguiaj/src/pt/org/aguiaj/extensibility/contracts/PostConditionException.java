package pt.org.aguiaj.extensibility.contracts;

public class PostConditionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PostConditionException(String message) {
		super(message);
	}
}
