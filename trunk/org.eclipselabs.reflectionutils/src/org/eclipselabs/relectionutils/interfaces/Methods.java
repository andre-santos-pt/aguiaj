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
import java.util.Arrays;

public class Methods {

	public static boolean sameSignature(Method m1, Method m2) {
		return 
			m1.getName().equals(m2.getName()) &&
			m1.getReturnType().equals(m2.getReturnType()) && 
			Arrays.deepEquals(m1.getParameterTypes(), m2.getParameterTypes());
	}
	
	

}
