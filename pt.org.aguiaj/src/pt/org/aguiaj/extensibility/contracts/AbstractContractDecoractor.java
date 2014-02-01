/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
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
