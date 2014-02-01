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
package org.eclipselabs.javainterpreter;

import java.util.Set;

public interface Context {

	Set<Class<?>> getImplicitClasses();
	
	boolean isClassAvailable(String name);
	
	Class<?> getClass(String name);
	
	boolean existsReference(String name);
	
	Class<?> referenceType(String name);
	
	Object getObject(String referenceName);

	void addReference(Class<?> type, String name, Object object);

	
}
