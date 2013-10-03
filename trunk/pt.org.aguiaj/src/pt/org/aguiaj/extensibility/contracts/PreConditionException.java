package pt.org.aguiaj.extensibility.contracts;

public class PreConditionException extends AbstractContractException implements ContractException {

	private static final long serialVersionUID = 1L;

	public PreConditionException(Class<?> clazz, String method, String message) {
		super(clazz, method, message);
	}
	
}
