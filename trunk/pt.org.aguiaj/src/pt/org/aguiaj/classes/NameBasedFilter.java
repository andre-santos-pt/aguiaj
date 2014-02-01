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
package pt.org.aguiaj.classes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class NameBasedFilter implements ClassMemberFilter {
	private Class<?> type;
	private String methodMatch;
	private boolean exclude;
	
	public NameBasedFilter(Class<?> type, List<String> methodNames, boolean exclude) {
		this.type = type;
		this.exclude = exclude;
		
		methodMatch = "";
		for(String name : methodNames) {
			if(!methodMatch.isEmpty())
				methodMatch += "|";
			methodMatch += name;
		}
	}
	
	@Override
	public Class<?> getTargetType() {
		return type;
	}

	@Override
	public boolean filter(Field field) {
		return false;
	}
	

	@Override
	public boolean filter(Method method) {
		if(exclude)
			return method.getName().matches(methodMatch);
		else
			return !method.getName().matches(methodMatch);
	}
	
	public String[] getMethodNames() {
		return methodMatch.split("\\|");
	}
}
