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

import java.lang.reflect.Array;

public class Output {
	
	public static String get(Object obj) {
		if(obj != null && obj.getClass().isArray())
			return handleArray(obj);
		else
			return toString(obj);
	}

	static String handleArray(Object array) {
		if(array == null || !array.getClass().isArray()) {
			return toString(array);
		}
		else {
			String s = "{";
			for(int i = 0; i < Array.getLength(array); i++) {
				if(!s.equals("{"))
					s += ", ";
				s += handleArray(Array.get(array, i));
			}
			s += "}";
			return s;
		}
	}

	static String toString(Object obj) {
		if(obj == null)
			return "null";
		else if(obj.getClass().equals(String.class))
			return "\"" + obj.toString() + "\"";
		else if(obj.getClass().equals(Character.class))
			return "'" + obj.toString() + "'";
		else
			return obj.toString();
	}
}

