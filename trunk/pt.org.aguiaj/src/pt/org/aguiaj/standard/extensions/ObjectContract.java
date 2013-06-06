package pt.org.aguiaj.standard.extensions;


import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;

public class ObjectContract extends Object implements ContractDecorator<Object> {

	public Object object;
	
	public ObjectContract(Object object) {
		this.object = object;
	}
	
	@Override
	public Object getWrappedObject() {
		return object;
	}
	
	@Override
	public boolean equals(Object arg) {
		boolean equals = object.equals(arg);
		
		if(equals && arg == null)
			throw new PostConditionException("The return value of equals(..) should be false if argument is null.");
		
		if(arg != null) {
			int hash = object.hashCode();
			int argHash = arg.hashCode();
			
			if(equals && hash != argHash)
				throw new PostConditionException("When equals(..) returs true for a given argument, hashCode() of both objects should return the same value.");
		}

		// reflexive
		
		// symmetric
		
		
		
		return equals;
	}
	
	
	
	@Override
	public int hashCode() {
		// constant
		
		return super.hashCode();
	
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	
	@Override
	public void checkInvariant() throws InvariantException {
		// TODO Auto-generated method stub
		
	}
	
}
