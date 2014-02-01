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
package pt.org.aguiaj.core.interpreter;

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.extensibility.Reference;


public class ExistingReference extends Expression implements Assignable {
	private String id;
	private Class<?> type;
	private Object object;
	
	public ExistingReference() {
		
	}
	
	public ExistingReference(String id, Class<?> type, Object object) {		
		this.id = id;
		this.type = type;
		this.object = object;
	}

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		if(!Common.isValidJavaIdentifier(text))
			return false;
		
		if(referenceTable.containsKey(text)) {
			id = text;	
			type = referenceTable.get(text).type;
			object = referenceTable.get(text).object;
			return true;
		}
		
		return false;
	}

	@Override
	public Class<?> type() {
		return type;
	}

	public String getIdentifier() {
		return id;
	}
	
	@Override
	public Object resolve() {
		return object;
	}

	@Override
	public boolean canAssign() {		
		return true;
	}

	@Override
	public String getExpressionText() {
		return getText();
	}
}
