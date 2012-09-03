/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface InspectionPolicy {
	boolean isClassVisible(Class<?> clazz);
	boolean isClassInstantiable(Class<?> clazz);
	boolean isStaticFieldVisible(Field field);	
	boolean isStaticMethodVisible(Method method);	
	boolean isConstructorVisible(Constructor<?> constructor);
	boolean isInstanceFieldVisible(Field field);
	boolean isCommandMethod(Method method);
}
