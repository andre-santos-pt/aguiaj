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
package org.eclipselabs.reflectionutils.wrappers;

import java.lang.reflect.Method;

public class TypeR {

	private final Class<?> type;
	
	public TypeR(Class<?> type) {
		if(type == null)
			throw new NullPointerException("argument cannot be null");
		
		this.type = type;
	}
	
	public final Class<?> getType() {
		return type;
	}
	
	public boolean hasMethod(Method m) {
		return true;
	}
	
	public boolean isSubtype(TypeR type) {
		return true;
	}
}
