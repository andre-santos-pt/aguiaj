/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.classes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassMemberFilter {
	Class<?> getTargetType();	
	boolean filter(Field field);
	boolean filter(Method method);
}
