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
package pt.org.aguiaj.common;

public class Reference {
	public final String name;
	public final Class<?> type;
	public final Object object;
	
	public Reference(String name, Class<?> type, Object object) {
		this.name = name;
		this.type = type;
		this.object = object;
	}
	
	public boolean isNull() {
		return object == null;
	}
	
	public boolean isEnum() {
		return type.isEnum();
	}
}