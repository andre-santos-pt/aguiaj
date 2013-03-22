/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import pt.org.aguiaj.core.Inspector;

public class StandardNamePolicy implements NamePolicy {

	private enum PropertyNameRule {
		IS, 
		GET, 
		TO;

		public boolean matches(String text) {
			return text.matches("^" + name().toLowerCase() + ".+");
		}

		public String apply(String text) {
			return text.substring(name().toLowerCase().length());
		}

		private static PropertyNameRule[] rules = values();
		
		public static String applyRule(String text) {
			for(PropertyNameRule rule : rules) {
				if(rule.matches(text))
					return rule.apply(text);
			}
			return text;
		}
	}
	
	public static String prettyPropertyName(Method method) {
		return firstToUpper(addSpaces(PropertyNameRule.applyRule(method.getName())));
	}


	public static String prettyCommandName(Method method) {
		return addSpaces(method.getName());
	}

	public static String prettyClassName(Class<?> clazz) {
		return addSpaces(clazz.getSimpleName());
	}

	public static String prettyWithPackage(Class<?> clazz) {
		return addSpaces(clazz.getName());
	}

	public static String prettyField(Field field) {
		return addSpaces(field.getName());
	}

	public static String baseReferenceName(Class<?> clazz) {
		String ref = null;
		
		if(clazz.isArray() && !clazz.getComponentType().isArray()) {
			ref = "array";
		}
		else if(clazz.isArray() && clazz.getComponentType().isArray()) {
			ref = "matrix";
		}
		else {	
			ref = clazz.getSimpleName().toLowerCase();
			if(ref.length() > 10)
				ref = ref.substring(0, 3);
		}
		
		return ref;
	}


	private static String addSpaces(String s) {
		String ret = "";
		for(int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			char next = i == s.length() - 1 ? '_' : s.charAt(i + 1);
			if(Character.isUpperCase(c) && !Character.isUpperCase(next) && next != '_' && i != 0 || c == '[' || c == ']')
				ret += " ";
			ret += c;
		}

		return ret;
	}

	public static String signature(Field field) {
		return field.getType().getSimpleName() + " " + field.getName();
	}

	public static String signature(Method method) {
		return method.getReturnType().getSimpleName() + " " + method.getName() + 
		params(method.getParameterTypes());
	}

	public static String signature(Constructor<?> constructor) {
		return constructor.getDeclaringClass().getSimpleName() + params(constructor.getParameterTypes()) + " (constructor)";
	}

	public static String getMethodToolTip(Object object, Method method, boolean inherited, boolean overriding) {
		String toolTip = StandardNamePolicy.signature(method);

		if(object != null) { 
			if(inherited) {
				Class<?> from = method.getDeclaringClass();
				toolTip += "\n(inherited method from class " + from.getSimpleName() + ")";
			}
			else if(overriding) {
				Class<?> from = Inspector.getOverridenMethodOwner(object.getClass(), method);
				toolTip += "\n(overriding method of class " + from.getSimpleName() + ")";
			}
			else {
				toolTip += "\n(method)";
			}
		}
		return toolTip;
	}

	private static String params(Class<?>[] params) {
		String ret = "(";
		for(int i = 0; i < params.length; i++) {
			if(i != 0)
				ret += ", ";
			ret += params[i].getSimpleName();
		}
		return ret + ")";
	}

	//	private static String firstToLower(String s) {
	//		return s.substring(0, 1).toLowerCase() + s.substring(1);
	//	}

	private static String firstToUpper(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
