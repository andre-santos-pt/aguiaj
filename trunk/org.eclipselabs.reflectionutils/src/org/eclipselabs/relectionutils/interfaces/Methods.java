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
