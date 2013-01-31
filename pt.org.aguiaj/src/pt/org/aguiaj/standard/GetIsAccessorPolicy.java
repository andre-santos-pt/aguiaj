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
package pt.org.aguiaj.standard;

import java.lang.reflect.Method;

import pt.org.aguiaj.extensibility.AccessorMethodDetectionPolicy;

public class GetIsAccessorPolicy implements AccessorMethodDetectionPolicy {

	@Override
	public boolean isAccessorMethod(Method method) {
		return isGetter(method) || isBooleanProperty(method);
	}
	
	private static final String CAPITAL_LETTER =
		"[A-ZËçÌåƒæêîÍïò]";
	

	private static final String LETTER = CAPITAL_LETTER.toLowerCase();

	
	private static final String WORD_STARTING_CAPITAL = 
		CAPITAL_LETTER + "(" + CAPITAL_LETTER + "|" + LETTER + ")*";
	
	private boolean isGetter(Method method) {
		return 
		!method.getDeclaringClass().equals(Object.class) &&
		!method.getReturnType().equals(void.class) && 		
		method.getName().matches("(get)" + WORD_STARTING_CAPITAL);
//		method.getName().matches("(get)[A-Z]([A-Za-z])*");
	}

	private boolean isBooleanProperty(Method method) {
		return
		!method.getDeclaringClass().equals(Object.class) &&
		method.getReturnType().equals(boolean.class) &&
		method.getName().matches("(is)[A-Z]([A-Za-z])*");
	}
}
