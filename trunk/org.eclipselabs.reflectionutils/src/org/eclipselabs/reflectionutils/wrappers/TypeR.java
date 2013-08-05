package org.eclipselabs.reflectionutils.wrappers;

import java.lang.reflect.Method;
import java.util.HashMap;

public class TypeR {

	private final Class<?> type;
	
	public TypeR(Class<?> type) {
		if(type == null)
			throw new NullPointerException("argument cannot be null");
		
		this.type = type;
	}
	
	public final Class<?> getType() {
		return type;
	}
	
	public boolean hasMethod(Method m) {
		return true;
	}
	
	public boolean isSubtype(TypeR type) {
		return true;
	}
}
