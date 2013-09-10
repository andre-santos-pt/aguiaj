package aguiaj.adt;

import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import pt.org.aguiaj.extensibility.contracts.PreConditionException;

public class StackContract implements ContractDecorator<Stack>, Stack {

	private final Stack stack;
	
	public StackContract(Stack stack) {
		this.stack = stack;
	}
	
	@Override
	public Stack getWrappedObject() {
		return stack;
	}
	
	// checked by CollectionContract
	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}
	
	// checked by CollectionContract
	@Override
	public int getSize() {
		return stack.getSize();
	}
	
	@Override
	public void checkInvariant() {

	}
	
	@Override
	public Object pop() {
		boolean empty = isEmpty();
		
		if(empty)
			throw new PreConditionException("stack is empty");
		
		int size = getSize();
		Object o = stack.pop();
		
		if(getSize() != size - 1)
			throw new PostConditionException("after a push, size must be decremented by one");
		
		if(o == null)
			throw new PostConditionException("pop() shoud never return null");
		
		return o;
	}

	@Override
	public void push(Object object) {
		int size = getSize();	
		stack.push(object);
		
		if(object == null)
			throw new PreConditionException("the stack cannot contain nulls");
		
		if(getSize() != size + 1)
			throw new PostConditionException("after a push, size must be incremented by one");
	}

	

}
