package pt.org.aguiaj.extensibility.contracts;

public class PostConditionException extends RuntimeException implements ContractException{

	private static final long serialVersionUID = 1L;

	public PostConditionException(String message) {
		super(message);
	}
}
