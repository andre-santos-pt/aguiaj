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
package pt.org.aguiaj.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pt.org.aguiaj.classes.ClassHierarchyComparator;
import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.MethodNameComparator;
import pt.org.aguiaj.extensibility.AccessorMethodDetectionPolicy;
import pt.org.aguiaj.standard.StandardInspectionPolicy;

public class Inspector {

	private InspectionPolicy inspectionPolicy;

	public Inspector(InspectionPolicy inspectionPolicy) {
		this.inspectionPolicy = inspectionPolicy;
	}

	public InspectionPolicy getPolicy() {
		return inspectionPolicy;
	}

	public List<Constructor<?>> getVisibleConstructors(Class<?> clazz) {
		List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
		if(!clazz.isInterface() && inspectionPolicy.isClassInstantiable(clazz)) {
			for(Constructor<?> c : clazz.getDeclaredConstructors())
				if(inspectionPolicy.isConstructorVisible(c))
					constructors.add(c);
		}
		Collections.sort(constructors,new Comparator<Constructor<?>>() {
			@Override
			public int compare(Constructor<?> c1, Constructor<?> c2) {
				return new Integer(c1.getParameterTypes().length).compareTo(c2.getParameterTypes().length);
			}
		});
		return constructors;
	}

	public List<Method> getVisibleStaticMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		for(Method m : clazz.getDeclaredMethods())
			if(inspectionPolicy.isStaticMethodVisible(m))
				methods.add(m);

		Collections.sort(methods, new MethodNameComparator());
		return methods;
	}

	public List<Field> getInvisibleInstanceAttributes(Class<?> clazz) {
		List<Field> privateFields = new ArrayList<Field>();
		for(Field f : getFieldsIncludingSuper(clazz)) {
			if(!Modifier.isStatic(f.getModifiers()) && 
			   !inspectionPolicy.isInstanceFieldVisible(f)) {
				f.setAccessible(true);
				privateFields.add(f);
			}
		}

		return privateFields;
	}

	private List<Field> getFieldsIncludingSuper(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		if(clazz == null) {

		}
		else if(clazz.isInterface()) {
			for(Class<?> superInterface : clazz.getInterfaces()) {
				fields.addAll(getFieldsIncludingSuper(superInterface));
			}
		}
		else {
			if(!clazz.equals(Object.class)) {
				for(Field f : clazz.getDeclaredFields()) {
					fields.add(f);
					f.setAccessible(true);
				}
				fields.addAll(getFieldsIncludingSuper(clazz.getSuperclass()));
			}
		}
		return fields;
	}

	public List<Field> getVisibleInstanceAttributes(Class<?> clazz) {
		List<Field> fields = ReflectionUtils.getAllInstanceFields(clazz);
		
		Iterator<Field> it = fields.iterator();
		while(it.hasNext()) {
			Field f = it.next();
			if(!inspectionPolicy.isInstanceFieldVisible(f))
				it.remove();
			else
				f.setAccessible(true);
		}
		
		return fields;
	}
	
	public List<Field> getVisibleStaticAttributes(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		
		for(Field f : clazz.getDeclaredFields()) {
			if(inspectionPolicy.isStaticFieldVisible(f)) {
				f.setAccessible(true);
				fields.add(f);
			}
		}
		
		return fields;
	}
	
	public List<Field> getEnumFields(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		if(!clazz.isEnum()) {
			return Collections.emptyList();
		}
		else {
			for(Field f : clazz.getFields())
				if(f.isEnumConstant()) {
					f.setAccessible(true);
					fields.add(f);
				}
		}
		return fields;	
	}


	public static List<Method> getAccessorMethods(Class<?> clazz) {
		Collection<Method> allMethods = ReflectionUtils.getAllMethods(clazz);

		List<Method> queryMethods = new ArrayList<Method>();
		AccessorMethodDetectionPolicy accessorPolicy = AguiaJActivator.getInstance().getAccessorPolicy();

		for(Method method : allMethods) {
			if(StandardInspectionPolicy.isVisible(method) &&
				method.getParameterTypes().length == 0 && 
				accessorPolicy.isAccessorMethod(method))
				
				queryMethods.add(method);
		}

		Collections.sort(queryMethods, new MethodNameComparator());

		return queryMethods;
	}

	public List<Method> getCommandMethods(Class<?> clazz) {
		Collection<Method> allMethods = ReflectionUtils.getAllMethods(clazz);
		List<Method> commandMethods = new ArrayList<Method>();
		List<Method> queryMethods = getAccessorMethods(clazz);

		for(Method method : allMethods)
			if(inspectionPolicy.isCommandMethod(method) && !queryMethods.contains(method))
				commandMethods.add(method);

		Collections.sort(commandMethods, new MethodNameComparator());
		return commandMethods;
	}


	public Set<Method> methodsOfSupertype(Class<?> type, Class<?> superType) {
		if(!superType.isAssignableFrom(type))
			throw new IllegalArgumentException("second argument is not a super type of first argument");
		
		Set<Method> methods = new HashSet<Method>();
		
		List<Class<?>> types = new ArrayList<Class<?>>();
		types.add(superType);
		types.addAll(superType.isInterface() ? superInterfaces(superType) : superClasses(superType));
		
		for(Class<?> t : types) {
				for(Method m : t.getMethods())
					try {
						methods.add(type.getDeclaredMethod(m.getName(), m.getParameterTypes()));
					} catch (NoSuchMethodException e) {
//						e.printStackTrace();
					} catch (SecurityException e) {
//						e.printStackTrace();
					}
		}
		
		return methods;
	}

//	private static boolean belongsToInterface(Class<?> interfacce, Method method) {
//		try {
//			interfacce.getDeclaredMethod(method.getName(), method.getParameterTypes());
//			return true;
//		} catch (NoSuchMethodException e) {
//			return false;
//		}
//	}

	// to lib
	public static List<Class<?>> superClasses(Class<?> clazz) {
		Class<?> superClass = clazz.getSuperclass();
		List<Class<?>> list = new ArrayList<Class<?>>();

		if(superClass != null && !superClass.equals(Object.class)) {
			list.add(superClass);
			list.addAll(superClasses(superClass));
		}

		return list;
	}

	// to lib
	public static List<Class<?>> superInterfaces(Class<?> interfacce) {
		List<Class<?>> list = new ArrayList<Class<?>>();

		for(Class<?> i : interfacce.getInterfaces()) {
			list.add(i);
			list.addAll(superInterfaces(i));
		}

		return list;
	}


	

	public static boolean isInherited(Class<?> clazz, Field field) {
		return !field.getDeclaringClass().equals(clazz);
	}

	public static boolean isInherited(Class<?> clazz, Method method) {
		return 
//		method.getDeclaringClass().equals(clazz) && method.isBridge() ||
		!method.getDeclaringClass().equals(clazz);  //&& !method.isBridge();
	}
	
	interface I {
		Object m();
		String t();
	}
	static class A implements I {
		
		public Object m() {
			return "?";
		}

		public String t() {
			return null;
		}
		
		public Object z() {
			return null;
		}
	}
	static class B extends A {
		public CharSequence m() {
			return "";
		}
		
		public List z() {
			return null;
		}
	}
	
	static class C extends A {
		public String m() {
			return "!";
		}
		
		@Override
		public int hashCode() {
			return super.hashCode();
		}
	}


	public static boolean isOverriding(Class<?> clazz, Method method) {
		return
				!Modifier.isStatic(method.getModifiers()) && 
//				!method.isBridge() &&
//				!method.isSynthetic() &&
				method.getDeclaringClass().equals(clazz) &&
				superTypeHasMethod(clazz.getSuperclass(), method); // problem?
	}



	private static boolean superTypeHasMethod(Class<?> clazz, Method method) {
		if(clazz == null) {
			return false;
		}
		else {
			for(Method m : clazz.getDeclaredMethods()) {
				if(isSame(m, method) && !Modifier.isAbstract(m.getModifiers()))
					return true;
			}
			return superTypeHasMethod(clazz.getSuperclass(), method);
		}			
	}

	// to lib
	public static Class<?> getOverridenMethodOwner(Class<?> clazz, Method method) {
		assert isOverriding(clazz, method);

		Class<?> superClass = clazz.getSuperclass();

		while(superClass != null) {
			for(Method m : superClass.getDeclaredMethods()) {
				if(isSame(m, method))
					return superClass;
			}
			superClass = superClass.getSuperclass();
		}

		return null;
	}

	// to lib
	public static boolean isSame(Method m1, Method m2) {
		return 
				m1.getName().equals(m2.getName()) &&
				Arrays.deepEquals(m1.getParameterTypes(), m2.getParameterTypes());
	}

	public boolean isStaticClass(Class<?> clazz) {
		return !inspectionPolicy.isClassInstantiable(clazz);
	}

	public boolean isClassVisible(Class<?> clazz) {
		return inspectionPolicy.isClassVisible(clazz);
	}


	public static List<Class<?>> getAllCompatibleTypes(Class<?> clazz) {
		List<Class<?>> superClasses = superClasses(clazz);
		Set<Class<?>> typeSet = new HashSet<Class<?>>();

		if(ClassModel.getInstance().hasSubClasses(clazz))
			typeSet.add(clazz);

		typeSet.addAll(superClasses);
		typeSet.addAll(superInterfaces(clazz));

		for(Class<?> superClass : superClasses) {
			for(Class<?> interfacce : superClass.getInterfaces()) {
				typeSet.add(interfacce);
				typeSet.addAll(superInterfaces(interfacce));
			}
		}

		List<Class<?>> types = new ArrayList<Class<?>>(typeSet);
		Collections.sort(types, new ClassHierarchyComparator());		
		return types;
	}
}
