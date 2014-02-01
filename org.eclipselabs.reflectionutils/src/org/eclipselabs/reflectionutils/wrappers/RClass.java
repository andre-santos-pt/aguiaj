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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RClass extends TypeR {

	private static Map<Class<?>, RClass> instances;
	
	static {
		instances = new HashMap<Class<?>, RClass>();
	}
	
	private RClass(Class<?> clazz) {
		super(clazz);
		if(clazz.isInterface())
			throw new IllegalArgumentException("not a class");
	}
	
	public static RClass get(Class<?> clazz) {
		if(instances.containsKey(clazz))
			return instances.get(clazz);
		else {
			RClass c = new RClass(clazz);
			instances.put(clazz, c);
			return c;
		}
	}
	
	/**
	 * ordered
	 */
	public List<RClass> getAllSuperClasses() {
		return null;
	}
	
	public List<Method> getInterfaceMethods(RInterface interfacce) {
		return null;
	}
	
	public boolean isInterfaceImplicit(RInterface interfacce) {
		return true;
	}
}
