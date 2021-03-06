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
package pt.org.aguiaj.extensibility;

import java.lang.reflect.Field;

public class Reference {
	public final String name;
	public final Class<?> type;
	public final Object object;
	public final Object previousObject;
	
	public Reference(String name, Class<?> type, Object object, Object previousObject) {
		if(name == null)
			throw new IllegalArgumentException("name cannot be null");
		
		if(type == null)
			throw new IllegalArgumentException("type cannot be null");
		
		this.name = name;
		this.type = type;
		this.object = object;
		this.previousObject = previousObject;
	}
	
	public Reference(String name, Class<?> type, Object object) {
		this(name, type, object, null);
	}
	
	public boolean isNull() {
		return object == null;
	}
	
	public boolean isEnum() {
		return type.isEnum();
	}
	
	public boolean isFieldReference() {
		return isFieldReference(name);
	}
	
	public static String staticReference(Class<?> clazz, Field field) {
		return clazz.getSimpleName() + "." + field.getName();
	}

	public static boolean isFieldReference(String ref) {
		return ref.indexOf('.') != -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof Reference))
			return false;
		
		Reference r = (Reference) obj;
		return name.equals(r.name) && type.equals(r.type);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public String toString() {
		return name + " : " + type.getSimpleName() + " -> " + object;
	}
}
