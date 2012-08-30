/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.standard;

import java.lang.reflect.Method;

import pt.org.aguiaj.extensibility.AccessorMethodDetectionPolicy;

public class GetIsAccessorPolicy implements AccessorMethodDetectionPolicy {

	@Override
	public boolean isAccessorMethod(Method method) {
		return isGetter(method) || isBooleanProperty(method);
	}
	
	private boolean isGetter(Method method) {
		return 
		!method.getDeclaringClass().equals(Object.class) &&
		!method.getReturnType().equals(void.class) && 		
		method.getName().matches("(get)[A-Z]([A-Za-z])*");
	}

	private boolean isBooleanProperty(Method method) {
		return
		!method.getDeclaringClass().equals(Object.class) &&
		method.getReturnType().equals(boolean.class) &&
		method.getName().matches("(is)[A-Z]([A-Za-z])*");
	}
}
