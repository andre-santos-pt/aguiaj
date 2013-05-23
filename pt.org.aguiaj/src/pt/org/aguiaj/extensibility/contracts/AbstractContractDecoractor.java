package pt.org.aguiaj.extensibility.contracts;

public abstract class AbstractContractDecoractor<T> implements ContractDecorator<T>{

	protected final T instance;
	
	public AbstractContractDecoractor(T instance) {
		if(instance == null)
			throw new NullPointerException("argument cannot be null");
		
		this.instance = instance;
	}
	
	public void checkInvariant() throws InvariantException {
		
	}
	
	public T getWrappedObject() {
		return instance;
	}
	
	public boolean validate(Object instance) {
		return true;
	}
}
