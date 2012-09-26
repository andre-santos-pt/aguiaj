/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import pt.org.aguiaj.core.InspectionPolicy;

public class StandardInspectionPolicy implements InspectionPolicy {
	
	
	private static boolean isVisible(Class<?> clazz) {
		return 
		!clazz.isSynthetic() &&
		!clazz.isAnonymousClass() &&		
		(isOnDefaultPackage(clazz) && !Modifier.isPrivate(clazz.getModifiers())
			|| Modifier.isPublic(clazz.getModifiers()));
	}
	
	private static boolean isVisible(Method method) {
		return isOnDefaultPackage(method.getDeclaringClass()) ?
				!Modifier.isPrivate(method.getModifiers()) 
				: 
				isVisible(method.getModifiers());
	}
	
	private static boolean isOnDefaultPackage(Class<?> clazz) {
		return clazz.getPackage() == null;
	}
	
	
	
	private static boolean isVisible(int modifiers) {
		boolean visible = Modifier.isPublic(modifiers);
		
//		if(!visible && AguiaJParam.PROTECTED_VISIBLE.getBoolean())
//			visible = Modifier.isProtected(modifiers);
//			
//		if(!visible && AguiaJParam.PACKAGEDEF_VISIBLE.getBoolean())
//			visible = isPackageDefault(modifiers);
					
		return visible;
	}
	
	

	
	
	public boolean isClassVisible(Class<?> clazz) {
		return isVisible(clazz);
	}
	
	public boolean isClassInstantiable(Class<?> clazz) {
		return
			clazz.equals(Object.class) ||
			!clazz.getSuperclass().equals(Object.class) || // inherits
			clazz.getDeclaredConstructors().length > 1 ||
			(clazz.getDeclaredConstructors().length == 1 && clazz.getDeclaredConstructors()[0].getParameterTypes().length > 0) ||
			hasInstanceFields(clazz) ||
			hasInstanceMethods(clazz) ||
			isEmpty(clazz);									
	}
	
	public boolean isStaticMethodVisible(Method method) {
		return		
		!method.isSynthetic() &&
		Modifier.isStatic(method.getModifiers()) && 
		isVisible(method) &&		
		!isMainMethod(method);
	}

	
	public boolean isStaticFieldVisible(Field field) {
		return
		!field.isSynthetic() &&
		Modifier.isStatic(field.getModifiers()) &&
		isVisible(field.getModifiers()); 
	}

	public boolean isConstructorVisible(Constructor<?> constructor) {
		return 	
		isOnDefaultPackage(constructor.getDeclaringClass()) ?
				!Modifier.isPrivate(constructor.getModifiers())				
				:
				isVisible(constructor.getDeclaringClass()) &&
				isVisible(constructor.getModifiers());
	}
	
	public boolean isInstanceFieldVisible(Field field) {
		return
		!field.isSynthetic() &&
		!Modifier.isStatic(field.getModifiers()) &&	
		isOnDefaultPackage(field.getDeclaringClass()) ?
				!Modifier.isPrivate(field.getModifiers())	
				:
				isVisible(field.getModifiers());
	}

	public boolean isCommandMethod(Method method) {
		return 
		!method.isSynthetic() &&
		!Modifier.isStatic(method.getModifiers()) &&		
		isVisible(method) &&
		!method.getDeclaringClass().equals(Object.class) &&	
		!isHashCode(method) &&
		!isToString(method)
		||
		isEqualsMethod(method) && !method.getDeclaringClass().equals(Object.class);
	}


	
	
	
	
	
	
	
	private static boolean isMainMethod(Method method) {
		return checkStaticSignature(method, "main", void.class, String[].class);			
	}
	
	private boolean isToString(Method method) {
		return checkInstanceSignature(method, "toString", String.class);
	}
	
	private static boolean isEqualsMethod(Method method) {		
		return checkInstanceSignature(method, "equals", boolean.class, Object.class);		
	}
	
	private boolean isHashCode(Method method) {
		return checkInstanceSignature(method, "hashCode", int.class);
	}
	
	
	
	// no instance/static fields, no instance/static methods
	private static boolean isEmpty(Class<?> clazz) {
		return !hasInstanceFields(clazz) && !hasInstanceMethods(clazz) && !hasStaticFields(clazz) && !hasStaticMethods(clazz);
	}
	
	// does not include inherited
	private static boolean hasInstanceFields(Class<?> clazz) {
		for(Field f : clazz.getDeclaredFields())
			if(!Modifier.isStatic(f.getModifiers()))
				return true;
		return false;
	}
	
	// does not include inherited	
	private static boolean hasInstanceMethods(Class<?> clazz) {
		for(Method m : clazz.getDeclaredMethods())
			if(!Modifier.isStatic(m.getModifiers()))
				return true;
		return false;
	}
	
	// does not include inherited
	private static boolean hasStaticMethods(Class<?> clazz) {
		for(Method m : clazz.getDeclaredMethods())
			if(Modifier.isStatic(m.getModifiers()))
				return true;
		return false;
	}

	// does not include inherited
	private static boolean hasStaticFields(Class<?> clazz) {
		for(Field f : clazz.getDeclaredFields())
			if(Modifier.isStatic(f.getModifiers()))
				return true;
		return false;
	}
	
	
	
	private static boolean checkInstanceSignature(
			Method method,
			String name,
			Class<?> returnType,
			Class<?> ... argTypes) {
		return checkSignature(method, name, false, returnType, argTypes);
	}
	
	private static boolean checkStaticSignature(
			Method method,
			String name,
			Class<?> returnType,
			Class<?> ... argTypes) {
		return checkSignature(method, name, true, returnType, argTypes);
	}
	
	private static boolean checkSignature(
			Method method,
			String name,
			boolean isStatic,
			Class<?> returnType,
			Class<?> ... argTypes) {
		
		Class<?>[] paramTypes = method.getParameterTypes();
		
		if(!method.getName().equals(name) ||
			isStatic != Modifier.isStatic(method.getModifiers()) ||
			!method.getReturnType().equals(returnType) ||
			paramTypes.length != argTypes.length)
			return false;
		
		for(int i = 0; i < argTypes.length; i++)
			if(!argTypes[i].equals(paramTypes[i]))
				return false;
			
		return true;
	}
	
	
	// idea: compatible signature	
}
