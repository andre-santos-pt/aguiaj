package aguiaj.adt;

import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;

public class CollectionContract implements ContractDecorator<Collection>, Collection {

	private Collection collection;
	
	public CollectionContract(Collection collection) {
		this.collection = collection;
	}
	
	@Override
	public Collection getWrappedObject() {
		return collection;
	}
	
	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}
	
	@Override
	public int getSize() {
		int size = collection.getSize();
		
		if(size < 0)
			throw new PostConditionException(Collection.class, "getSize", "size cannot be negative");
		
		return size;
	}
	
	@Override
	public void checkInvariant() {
		if(isEmpty() && getSize() != 0)
			throw new InvariantException("when the stack is empty, size must be zero");
		
		if(!isEmpty() && getSize() <= 0)
			throw new InvariantException("when the stack is not empty, size must be greater than zero");
	}

	

}
