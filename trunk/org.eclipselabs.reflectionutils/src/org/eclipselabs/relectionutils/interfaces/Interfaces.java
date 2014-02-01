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
package org.eclipselabs.relectionutils.interfaces;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Interfaces {


	
	
	public static boolean declaresMethod(Class<?> interfacce, Method method) {
		if(!interfacce.isInterface())
			throw new IllegalArgumentException("not an interface");
		
		try {
			interfacce.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	public static List<Class<?>> superInterfaces(Class<?> interfacce) {
		if(!interfacce.isInterface())
			throw new IllegalArgumentException("not an interface");
		
		List<Class<?>> list = new ArrayList<Class<?>>();

		for(Class<?> i : interfacce.getInterfaces()) {
			list.add(i);
			list.addAll(superInterfaces(i));
		}

		return list;
	}
}
