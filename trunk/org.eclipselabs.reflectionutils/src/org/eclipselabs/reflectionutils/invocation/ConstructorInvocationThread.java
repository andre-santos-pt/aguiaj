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
package org.eclipselabs.reflectionutils.invocation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class ConstructorInvocationThread extends InvocationThread {
	
	private Constructor<?> constructor;
	private Object[] args;
	
	public ConstructorInvocationThread(Constructor<?> constructor, Object[] args) {
		this.constructor = constructor;
		this.args = args;
	}

	@Override
	protected Object execute() throws InvocationTargetException {
		constructor.setAccessible(true);
		try {
			return constructor.newInstance(args);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
