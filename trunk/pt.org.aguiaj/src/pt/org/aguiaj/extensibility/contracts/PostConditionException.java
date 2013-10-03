package pt.org.aguiaj.extensibility.contracts;

public class PostConditionException extends AbstractContractException implements ContractException {

	private static final long serialVersionUID = 1L;

	public PostConditionException(Class<?> clazz, String method, String message) {
		super(clazz, method, message);
	}
	
}
