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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.extensibility.AccessorMethodDetectionPolicy;
import pt.org.aguiaj.standard.StandardInspectionPolicy;
import pt.org.aguiaj.standard.StandardNamePolicy;

public class Inspector {
	
	private static InspectionPolicy inspectionPolicy;
	
	static {
		loadInspectionPolicy();
	}

	public static void loadInspectionPolicy() {
		try {
			Class<?> inspectionPolicyClass = Class.forName(AguiaJParam.INSPECTION_POLICY.getString());
			inspectionPolicy = (InspectionPolicy) inspectionPolicyClass.newInstance();

		} catch (Exception e) {
			e.printStackTrace();
			inspectionPolicy = new StandardInspectionPolicy();
		}		
	}

	public static InspectionPolicy getInspectionPolicy() {
		return inspectionPolicy;
	}

	public static List<Constructor<?>> getVisibleConstructors(Class<?> clazz) {
		List<Constructor<?>> constructors = new ArrayList<Constructor<?>>();
		if(inspectionPolicy.isClassInstantiable(clazz)) {
			for(Constructor<?> c : clazz.getDeclaredConstructors())
				if(inspectionPolicy.isConstructorVisible(c))
					constructors.add(c);
		}
		return constructors;
	}

	public static List<Method> getVisibleStaticMethods(Class<?> clazz) {
		List<Method> methods = new ArrayList<Method>();
		for(Method m : clazz.getDeclaredMethods())
			if(inspectionPolicy.isStaticMethodVisible(m))
				methods.add(m);

		return methods;
	}


	public static List<Field> getVisibleAttributes(Class<?> clazz) {
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(getVisibleAttributes(clazz, true));
		fields.addAll(getVisibleAttributes(clazz, false));
		return fields;
	}

	public static List<Field> getInvisibleInstanceAttributes(Class<?> clazz) {
		List<Field> privateFields = new ArrayList<Field>();
		for(Field f : getFieldsIncludingSuper(clazz)) {
			if(!inspectionPolicy.isInstanceFieldVisible(f) && !Modifier.isStatic(f.getModifiers()))
				privateFields.add(f);
		}

		return privateFields;
	}

	private static List<Field> getFieldsIncludingSuper(Class<?> clazz) {
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
					f.setAccessible(true);
					fields.add(f);	
				}
				fields.addAll(getFieldsIncludingSuper(clazz.getSuperclass()));
			}
		}
		return fields;
	}

	public static List<Field> getVisibleAttributes(Class<?> clazz, boolean staticFields) {
		List<Field> allFields = new ArrayList<Field>();
		List<Field> publicFields = Arrays.asList(clazz.getFields());
		allFields.addAll(publicFields);

		for(Field f : clazz.getDeclaredFields())
			if(!publicFields.contains(f))
				allFields.add(f);

		for(Field field : allFields.toArray(new Field[allFields.size()])) {
			if(staticFields) {
				if(!Modifier.isStatic(field.getModifiers()) || !inspectionPolicy.isStaticFieldVisible(field))
					allFields.remove(field);
				else
					field.setAccessible(true);
			}
			else {
				if(Modifier.isStatic(field.getModifiers()) || !inspectionPolicy.isInstanceFieldVisible(field))
					allFields.remove(field);
				else
					field.setAccessible(true);
			}
		}

		return allFields;
	}

	public static List<Method> getAccessorMethods(Class<?> clazz) {
//		List<Method> allMethods = new ArrayList<Method>();
//		addAllNonPrivateMethods(clazz, allMethods, false);

		Collection<Method> allMethods = ReflectionUtils.getAllMethods(clazz);
		
		List<Method> queryMethods = new ArrayList<Method>();
		AccessorMethodDetectionPolicy accessorPolicy = AguiaJActivator.getDefault().getAccessorPolicy();
		
		for(Method method : allMethods) {
			if(method.getParameterTypes().length == 0 && accessorPolicy.isAccessorMethod(method))
				queryMethods.add(method);
		}

		Collections.sort(queryMethods, new Comparator<Method>() {
			@Override
			public int compare(Method a, Method b) {
				String prettyA = StandardNamePolicy.prettyPropertyName(a);
				String prettyB = StandardNamePolicy.prettyPropertyName(b);
				return prettyA.compareTo(prettyB);
			}
		});

		return queryMethods;
	}

	public static List<Method> getCommandMethods(Class<?> clazz) {
//		List<Method> allMethods = new ArrayList<Method>();
//		addAllNonPrivateMethods(clazz, allMethods, false);

		Collection<Method> allMethods = ReflectionUtils.getAllMethods(clazz);
		
		
		List<Method> commandMethods = new ArrayList<Method>();
		
		List<Method> queryMethods = getAccessorMethods(clazz);
				
		for(Method method : allMethods)
			if(inspectionPolicy.isCommandMethod(method) && !queryMethods.contains(method))
				commandMethods.add(method);

		Collections.sort(commandMethods, new Comparator<Method>() {
			@Override
			public int compare(Method a, Method b) {
				return a.getName().compareTo(b.getName());
			}
		});

		return commandMethods;
	}


	public static Map<Class<?>,List<Method>> getCommandMethodsByType(Class<?> clazz) {
		Map<Class<?>, List<Method>> table = new LinkedHashMap<Class<?>, List<Method>>();

		List<Method> noInterfaceMethods = getCommandMethods(clazz);

		Set<Class<?>> interfacesAndSuperClasses = new HashSet<Class<?>>(Arrays.asList(clazz.getInterfaces()));
		interfacesAndSuperClasses.addAll(superClasses(clazz));

		for(Class<?> type : interfacesAndSuperClasses) {
			List<Method> methods = new ArrayList<Method>();

			for(Iterator<Method> it = noInterfaceMethods.iterator(); it.hasNext(); ) {
				Method method = it.next();
				if(belongsToInterface(type, method)) {
					methods.add(method);
					it.remove();
				}
			}

			if(methods.size() > 0)
				table.put(type, methods);
		}

		if(noInterfaceMethods.size() > 0)
			table.put(clazz, noInterfaceMethods);

		return table;
	}

	private static boolean belongsToInterface(Class<?> interfacce, Method method) {
		try {
			interfacce.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

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

//		public static List<Method> getAllMethods(Class<?> clazz) {
//			List<Method> allMethods = new ArrayList<Method>();
//	
//			for(Method m : clazz.getMethods()) {
//				m.setAccessible(true);
//				allMethods.add(m);
//			}
//	
//			for(Method m : clazz.getDeclaredMethods())
//				if(!allMethods.contains(m)) {
//					m.setAccessible(true);
//					allMethods.add(m);
//				}
//	
//			return allMethods;
//		}

//	public static void addAllNonPrivateMethods(Class<?> clazz, List<Method> allMethods, boolean staticMethods) {
//
//		if(clazz == null || clazz.equals(Object.class)) {
//			return;
//		}
//		else {
//			for(Method m : clazz.getDeclaredMethods()) {
//				int mod = m.getModifiers();
//				if(((staticMethods && Modifier.isStatic(mod)) || (!staticMethods && !Modifier.isStatic(mod))) &&
//						!m.isSynthetic() &&
//						!Modifier.isPrivate(mod) && 
//						!containsSameMethod(allMethods, m)) {
//					allMethods.add(m);					
//				}
//			}
//			if(clazz.isInterface()) {
//				for(Class<?> superInterface : clazz.getInterfaces())	
//					addAllNonPrivateMethods(superInterface, allMethods, staticMethods);	
//			}
//			else {
//				addAllNonPrivateMethods(clazz.getSuperclass(), allMethods, staticMethods);
//			}
//		}		
//	}

	private static boolean containsSameMethod(List<Method> list, Method m) {
		for(Method method : list) {
			if(isSame(m, method)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInherited(Class<?> clazz, Field field) {
		return !field.getDeclaringClass().equals(clazz);
	}
	
	public static boolean isInherited(Class<?> clazz, Method method) {
		return !method.getDeclaringClass().equals(clazz);
	}

	public static boolean isOverriding(Class<?> clazz, Method method) {
		return
		!Modifier.isStatic(method.getModifiers()) && 
		method.getDeclaringClass().equals(clazz) &&
		superTypeHasMethod(clazz.getSuperclass(), method);
	}
	
	

	private static boolean superTypeHasMethod(Class<?> clazz, Method method) {
		if(clazz == null) {
			return false;
		}
		else {
			for(Method m : clazz.getDeclaredMethods()) {
				if(isSame(m, method))
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

	public static boolean isStaticClass(Class<?> clazz) {
		return inspectionPolicy.isClassInstantiable(clazz);
	}

	public static boolean isClassVisible(Class<?> clazz) {
		return inspectionPolicy.isClassVisible(clazz);
	}


	public static List<Class<?>> getAllCompatibleTypes(Class<?> clazz) {
		List<Class<?>> superClasses = superClasses(clazz);
		Set<Class<?>> typeSet = new HashSet<Class<?>>();

		if(ClassModel.getInstance().hasSubClasses(clazz))
			typeSet.add(clazz);

		typeSet.addAll(superClasses);
		typeSet.addAll(Arrays.asList(clazz.getInterfaces()));

		for(Class<?> superClass : superClasses) {
			for(Class<?> interfacce : superClass.getInterfaces()) {
				typeSet.add(interfacce);
				typeSet.addAll(superInterfaces(interfacce));
			}
		}

		List<Class<?>> types = new ArrayList<Class<?>>(typeSet);
		Collections.sort(types, new ClassSorter());		
		return types;
	}

	
	
	public static class ClassSorter implements Comparator<Class<?>> {
		public int compare(Class<?> a, Class<?> b) {
			if(a.equals(b))
				return 0;
			else if(a.isAssignableFrom(b))
				return -1;
			else
				return 1;
		}

	}
}
