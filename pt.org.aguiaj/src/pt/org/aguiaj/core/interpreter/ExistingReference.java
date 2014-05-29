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
	
	private Class<?> cast;
	
	public ExistingReference() {
		
	}
	
	public ExistingReference(String id, Class<?> type, Object object) {		
		this.id = id;
		this.type = type;
		this.object = object;
	}

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) throws ParseException {
		text = text.replaceAll("\\s+", "");
		
		// cast
		if(text.matches("\\((.)+\\)(.)+")) {
			String castType = text.substring(1, text.indexOf(')'));
			for(Class<?> c : classSet)
				if(c.getSimpleName().equals(castType))
					cast = c;
			
			if(cast == null)
				return false;
			
			text = text.substring(text.indexOf(')')+1);
		}
		
		if(!Common.isValidJavaIdentifier(text))
			return false;
		
		if(referenceTable.containsKey(text)) {
			id = text;	
			type = referenceTable.get(text).type;
			object = referenceTable.get(text).object;
			
			if(cast != null && object != null && !type.isAssignableFrom(cast))
				throw new ParseException("Incompatible cast", type.getSimpleName() + " to " + cast.getSimpleName());
			
			return true;
		}
		
		return false;
	}

	@Override
	public Class<?> type() {
		return cast != null ? cast : type;
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
