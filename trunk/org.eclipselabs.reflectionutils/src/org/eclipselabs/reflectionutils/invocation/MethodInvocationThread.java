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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodInvocationThread extends InvocationThread {
	private Method method;
	private Object object;
	private Object[] args;

	public MethodInvocationThread(Method method, Object object, Object[] args) {
		this.method = method;
		this.object = object;
		this.args = args;
	}

	@Override
	protected Object execute() throws InvocationTargetException {
		method.setAccessible(true);
		try {
			return method.invoke(object, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
