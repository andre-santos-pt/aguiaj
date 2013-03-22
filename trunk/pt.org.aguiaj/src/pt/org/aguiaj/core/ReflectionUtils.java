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


import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;


public class ReflectionUtils {

	public static boolean declaresToString(Class<?> clazz) {
		try {
			return !clazz.getMethod("toString").getDeclaringClass().equals(Object.class);
		}
		catch(NoSuchMethodException e) { 
			return false;
		}
	}

	public static Method getToStringMethod(Class<?> clazz) {
		try {
			return clazz.getMethod("toString");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}

	public static boolean declaresEquals(Class<?> clazz) {
		try {
			clazz.getDeclaredMethod("equals", Object.class);
			return true;
		}
		catch(NoSuchMethodException e) { 
			return false;
		}
	}


	public static boolean isOneDimensionArray(Object array) {
		return array.getClass().isArray() && !array.getClass().getComponentType().isArray();	
	}

	public static boolean isMultiDimensionArray(Object array) {
		return array.getClass().isArray() && array.getClass().getComponentType().isArray();

	}

	public static boolean arrayEquals(Object a, Object b) {
		if(a == null && b == null)
			return true;

		if(a != null && b == null || a == null && b != null)
			return false;

		if(isOneDimensionArray(a) && isOneDimensionArray(b)) {
			int len = Array.getLength(a); 
			if(len != Array.getLength(b))
				return false;

			for(int i = 0; i < len; i++) {
				Object vA = Array.get(a, i);
				Object vB = Array.get(b, i);

				if(vA == null && vB != null)
					return false;

				if(!vA.equals(vB))
					return false;
			}
			return true;
		}
		else if(isMultiDimensionArray(a) && isMultiDimensionArray(b)) {
			return Arrays.deepEquals((Object[]) a, (Object[]) b);
		}

		return false;
	}


	public static List<Field> getAllInstanceFields(Class<?> clazz) {
		List<Field> all = new ArrayList<Field>();
		getAllInstanceFields_rec(clazz, all);
		return all;
	}

	private static void getAllInstanceFields_rec(Class<?> clazz, List<Field> all) {
		for(Field f : clazz.getDeclaredFields())
			if(!Modifier.isStatic(f.getModifiers()))
				all.add(f);

		Class<?> superClass = clazz.getSuperclass();
		if(superClass != null)
			getAllInstanceFields_rec(superClass, all);
	}

	public static Collection<Method> getAllMethods(Class<?> clazz) {
		Set<Method> all = new HashSet<Method>();
		getAllMethods_rec(clazz, all);
		return all;
	}



	private static void getAllMethods_rec(Class<?> clazz, Collection<Method> all) {
		for(Method m : clazz.getDeclaredMethods()) {
			if(!exists(m, all))
				all.add(m);
		}
		Class<?> superClass = clazz.getSuperclass();
		if(superClass != null)
			getAllMethods_rec(superClass, all);
	}


	private static boolean exists(Method m, Collection<Method> all) {
		for(Method existing : all)
			if(isSame(m, existing))
				return true;

		return false;
	}

	public static boolean isSame(Method m1, Method m2) {
		return 
				m1.getName().equals(m2.getName()) &&
				Arrays.deepEquals(m1.getParameterTypes(), m2.getParameterTypes());
	}

	public static boolean hasEquivalentMethod(Class<?> clazz, Method method) {
		try {
			clazz.getMethod(method.getName(), method.getParameterTypes());
			return true;
		}
		catch(NoSuchMethodException e) {
			return false;
		}
	}
	
	
	public static void loadClass(Class<?> clazz) {
		loadClass(clazz.getName());		
	}

	public static void loadClass(String fullClassName) {
		try {
			Class.forName(fullClassName);
		} 
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}


	private static Set<Class<?>> getClasses(IPath rootPath, String packageName) {		
		IPath path = rootPath;
		for(String level : packageName.split("\\."))
			path = path.append(level);

		File directory = path.toFile();

		Map<String, File> classFiles = new HashMap<String, File>();


		if(directory.exists()) {
			String[] files = directory.list();

			if(files != null) {
				for(int i = 0; i < files.length; i++) {
					if(files[i].endsWith(".class")) {
						String className = (packageName.equals("") ? "" : packageName + ".") + files[i].substring(0, files[i].length() - ".class".length());
						File file = new Path(directory.getAbsolutePath()).append(files[i]).toFile();							
						classFiles.put(className, file);
					}
				}
			}
		}
		else
			return Collections.emptySet();

		AguiaClassLoader classLoader = AguiaClassLoader.getInstance(classFiles);

		Set<Class<?>> classes = new HashSet<Class<?>>();

		for(String className : classFiles.keySet()) {
			try {
				Class<?> clazz = classLoader.loadClass(className);
				classes.add(clazz);								

				addInnerClasses(clazz, classes);
			} 
			catch (ClassNotFoundException e) {				

			}			
		}

		return classes;
	}

	private static void addInnerClasses(Class<?> clazz, Collection<Class<?>> classes) {
		for(Class<?> inner : clazz.getClasses()) {
			if(classes.contains(inner))
				return;
			
			classes.add(inner);
			addInnerClasses(inner, classes);
		}
	}

	public static Map<String, Set<Class<?>>> readClassFiles(IPath rootPath) {
		Map<String,Set<Class<?>>> packagesClasses = new LinkedHashMap<String,Set<Class<?>>>();
		readClassFilesAux(rootPath, rootPath, "", packagesClasses);			
		return packagesClasses;
	}

	private static void readClassFilesAux(IPath rootPath, IPath currentPath, String namespace, Map<String,Set<Class<?>>> packagesClasses) {
		Set<Class<?>> classList = ReflectionUtils.getClasses(rootPath, namespace);
		if(classList.size() > 0)
			packagesClasses.put(namespace, classList);

		if(!currentPath.toFile().exists() || !currentPath.toFile().isDirectory())
			return;

		for(File child : currentPath.toFile().listFiles()) {
			if(child.isDirectory()) {
				String nextNameSpace = namespace.equals("") ? child.getName() : namespace + "." + child.getName();
				IPath childPath = currentPath.append(child.getName());
				readClassFilesAux(rootPath, childPath, nextNameSpace, packagesClasses);
			}
		}
	}

	public static String getTextualRepresentation(Object object, boolean complete) {
		String ret = "...";

		if(object == null) {
			ret = "NULL";
		}
		else {
			Class<?> clazz = object.getClass();

			if(clazz.isArray()) {
				if(clazz.getComponentType().isArray()) {
					ret = Arrays.deepToString((Object[]) object);
					ret = ret.substring(1, ret.length() - 1);
				}
				else {
					if(clazz.equals(int[].class))
						ret = Arrays.toString((int[]) object);

					else if(clazz.equals(double[].class))
						ret = Arrays.toString((double[]) object);

					else if(clazz.equals(char[].class)) {
						//						ret = Arrays.toString((char[]) object);
						ret = new String((char[]) object);
					}

					else if(clazz.equals(boolean[].class))
						ret = Arrays.toString((boolean[]) object);

					else if(!clazz.getComponentType().isPrimitive()) {
						Object[] objs = (Object[]) object;
						ret = "[";

						for(int i = 0; i < objs.length; i++) {
							if(i != 0)
								ret += ", ";

							if(objs[i] == null)
								ret += "null";
							else if(declaresToString(objs[i].getClass())) {
								ret += objs[i].toString();
							}
							else {
								ret += "...";
							}

						}
						ret += "]";				
					}			
				}


			}	
			else {
//				Method toStringMethod = getToStringMethod(object.getClass());
//
//				if(!toStringMethod.getDeclaringClass().equals(Object.class)) {
//					MethodInvocationCommand command = new MethodInvocationCommand(object, "na", toStringMethod, new Object[0], new String[0]);
//					command.execute();
//
//					if(command.failed())
//						return null;
//
//					ret = (String) command.getResultingObject();
//				}		
				ret = getToStringResult(object);
			}

			// in case of an array or collection, crop the result
//			if(clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
				if(ret != null && !complete && ret.length() > 20)
					ret = ret.substring(0, 20) + "...";
//			}
		}

		return ret;
	}


	public static String getToStringResult(Object object) {
		Method toStringMethod = getToStringMethod(object.getClass());

		if(!toStringMethod.getDeclaringClass().equals(Object.class)) {
			MethodInvocationCommand command = new MethodInvocationCommand(object, "na", toStringMethod, new Object[0], new String[0]);
			command.execute();

			if(command.failed())
				return null;

			return (String) command.getResultingObject();
		}
		else {
			String nl = System.getProperty("line.separator");
			String ret = "";

			for(Field f : ClassModel.getInstance().getVisibleAttributes(object.getClass())) {
				if(f.getType().isPrimitive() || f.getType().equals(String.class)) {
					Object get = null;
					try {
						get = f.get(object);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					if(!ret.isEmpty())
						ret += nl;
					
					ret += f.getName() + ": " + (get == null ? "null" : get.toString());
				}
			}
			return ret;
		}

	}

	public static int arrayDim(Class<?> c) {
		if(c.isArray() && !c.getComponentType().isArray())
			return 1;
		else
			return 1 + arrayDim(c.getComponentType());
	}

	public static boolean isArrayDim(Class<?> c, int dim) {
		if(dim == 1 && c.isArray() && !c.getComponentType().isArray())
			return true;
		else
			return c.isArray() && isArrayDim(c.getComponentType(), dim - 1);
	}

	public static boolean equalArrayDim(Class<?> a, Class<?> b) {
		return arrayDim(a) == arrayDim(b);
	}


	public static void main(String[] args) {
		System.out.println(arrayDim(Object[][].class));
		System.out.println(arrayDim(int.class));
		System.out.println(arrayDim(Object[][][].class));

	}

	public static boolean checkParamTypes(Class<?>[] paramTypes, Object[] args) {
		if(paramTypes.length != args.length)
			return false;

		for(int i = 0; i < paramTypes.length; i++) {
			if(!paramTypes[i].isPrimitive() && args[i] != null && !paramTypes[i].isInstance(args[i]))
				return false;
			else if(paramTypes[i].equals(int.class) && !Integer.class.isInstance(args[i]))
				return false;
			else if(paramTypes[i].equals(double.class) && !Double.class.isInstance(args[i]))
				return false;
			else if(paramTypes[i].equals(boolean.class) && !Boolean.class.isInstance(args[i]))
				return false;
			else if(paramTypes[i].equals(char.class) && !Character.class.isInstance(args[i]))
				return false;
		}

		return true;
	}

	@SuppressWarnings("unused")
	public static boolean tryClass(Class<?> clazz) {
		try {
			for(Field f : clazz.getDeclaredFields())
				;
			for(Field f : clazz.getFields())
				;
			for(Constructor<?> c : clazz.getDeclaredConstructors())
				;
			for(Constructor<?> c : clazz.getConstructors())
				;			
			for(Method m : clazz.getDeclaredMethods())
				;
			for(Method m : clazz.getMethods())
				;	
		} catch (Throwable e) {
			//			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	


}
