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

