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

import java.util.Collection;

public class RInterface extends TypeR {

	public RInterface(Class<?> interfacce) {
		super(interfacce);
		if(!interfacce.isInterface())
			throw new IllegalArgumentException("not an interface");	
	}
	
	public Collection<RInterface> getAllSuperInterfaces() {
		return null;
	}
	
	
}
