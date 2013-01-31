/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.interpreter;

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;

public abstract class Literal extends Expression {

	private Class<?> type;
	protected Object value;
	
	protected Literal(Class<?> type) {
		this.type = type;
	}	
	
	@Override
	public Class<?> type() {
		return type;
	}

	@Override
	public Object resolve() {				
		return value;
	}

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {		
		return accept(text);
	}

	@Override
	public String toString() {	
		return value.toString();
	}
	
	public abstract boolean accept(String text);

}
