package org.eclipselabs.javainterpreter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ChildPropertyDescriptor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;


class ExpressionVisitor extends ASTVisitor {

	private Map<Expression, List<Object>> argsTable;
	private Object result;

	private Map<String,Class<?>> baseClasses;
	private Map<String,Object> refTable;
	private Map<String,Class<?>> refTypes;


	public ExpressionVisitor() {
		baseClasses = new HashMap<String, Class<?>>();
		refTable = new HashMap<String, Object>();
		refTypes = new HashMap<String, Class<?>>();
		argsTable = new HashMap<Expression, List<Object>>();
	}


	public void addBaseClass(Class<?> type) {
		baseClasses.put(type.getSimpleName(), type);
	}

	public void addReference(Class<?> type, String name, Object object) {
		refTable.put(name, object);
		refTypes.put(name, type);
	}


	public void clear() {
		refTable.clear();
		refTypes.clear();
	}


	public Object resolve() {
		return result;
	}

	@Override
	public boolean visit(ClassInstanceCreation node) {
		argsTable.put(node, new ArrayList<Object>(4));
		return true;
	}
	@Override
	public boolean visit(ArrayCreation node) {
		argsTable.put(node, new ArrayList<Object>(2));
		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		argsTable.put(node, new ArrayList<Object>(4));
		return true;
	}


	@Override
	public boolean visit(Assignment node) {
		argsTable.put(node, new ArrayList<Object>(1));
		return true;
	}


	@Override
	public void endVisit(Assignment node) {
		result = argsTable.get(node).get(0);
		addReference(result == null ? Object.class : result.getClass(), node.getLeftHandSide().toString(), result);
	}

	@Override
	public void endVisit(ClassInstanceCreation node) {
		Object r = resolve(node);
		if(node.getStartPosition() == 0)
			result = r;
		else
			argsTable.get(node.getParent()).add(r);
	}

	private Object resolve(ClassInstanceCreation node) {
		Type type = (Type) node.getStructuralProperty(ClassInstanceCreation.TYPE_PROPERTY);
		Class<?> clazz = loadClass(type);
		Object[] args = argsTable.get(node).toArray();
		Constructor<?> c = match(clazz, args);
		if(c != null) {
			try {
				return c.newInstance(args);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		throw new IllegalArgumentException("Constructor not found");
	}



	@Override
	public void endVisit(ArrayCreation node) {
		ArrayType type = (ArrayType) node.getStructuralProperty(ArrayCreation.TYPE_PROPERTY);

		Object[] args = argsTable.get(node).toArray();
		
		Object array = null;
		if(args.length == 1 && args[0].getClass().isArray()) {
			array = args[0];
		}
		else {
			int[] dims = new int[type.getDimensions()];
			for(int i = 0; i < dims.length; i++)
				dims[i] = (int) Double.parseDouble(args[i].toString());

			Type t = (Type) type.getStructuralProperty(ArrayType.COMPONENT_TYPE_PROPERTY);

			Class<?> clazz = loadClass(t);
			array = Array.newInstance(clazz, dims);
		}
		
		argsTable.get(node.getParent()).add(array);
	}

	@Override
	public void endVisit(MethodInvocation node) {
		Object r = resolve(node);
		if(node.getStartPosition() == 0)
			result = r;
		else
			argsTable.get(node.getParent()).add(r);
	}



	private static final Class<?>[] primitiveTypes = {
		int.class, double.class, long.class, short.class,
		byte.class, float.class, boolean.class, char.class
	};

	private Class<?> getPrimitiveType(String name) {
		for(Class<?> c : primitiveTypes)
			if(c.getName().equals(name))
				return c;

		throw new IllegalArgumentException("Not a primitive type");
	}

	private Class<?> loadClass(Type t) {
		String clazzName = t.toString();
		if(t.isPrimitiveType()) {
			return getPrimitiveType(clazzName);
		}
		else {
			try {
				return  Class.forName(clazzName);
			} catch (ClassNotFoundException e) {
				try {
					return Class.forName("java.lang." + clazzName);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}

			}
		}
		throw new IllegalArgumentException("Could not load type");
	}









	@Override
	public boolean visit(NumberLiteral node) {
		String token = node.getToken();
		ASTNode parent = node.getParent();

		if(node.getParent() instanceof PrefixExpression) {
			PrefixExpression exp = (PrefixExpression) node.getParent();
			if(exp.getOperator() == PrefixExpression.Operator.MINUS) {
				token = "-" + token;
				parent = exp.getParent();
			}
		}

		Number number = null;
		if(token.indexOf('.') == -1)
			number = new Integer(token);
		else
			number = new Double(token);

		if(argsTable.containsKey(parent))
			argsTable.get(parent).add(number);

		return true;
	}

	@Override
	public boolean visit(CharacterLiteral node) {
		Character c = new Character(node.charValue());
		argsTable.get(node.getParent()).add(c);
		return true;
	}

	@Override
	public boolean visit(StringLiteral node) {
		argsTable.get(node.getParent()).add(node.getLiteralValue());
		return true;
	}

	@Override
	public boolean visit(BooleanLiteral node) {
		argsTable.get(node.getParent()).add(new Boolean(node.booleanValue()));
		return true;
	}

	@Override
	public boolean visit(NullLiteral node) {
		argsTable.get(node.getParent()).add(null);
		return true;
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		System.out.println("ARRAY: " + node.getParent().getClass());
		argsTable.put(node, new ArrayList<Object>());
		return true;
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		System.out.println("ARRAY INIT: " + node + " :: " + node.getParent());
		ArrayType type = (ArrayType) node.getParent().getStructuralProperty(ArrayCreation.TYPE_PROPERTY);

		Type t = (Type) type.getStructuralProperty(ArrayType.COMPONENT_TYPE_PROPERTY);
		Class<?> clazz = loadClass(t);

		Object array = Array.newInstance(clazz, argsTable.get(node).size());
		for(int i = 0; i < argsTable.get(node).size(); i++) {
			System.out.println(i + " " + argsTable.get(node).get(i));
			Array.set(array, i, argsTable.get(node).get(i));
		}
		argsTable.get(node.getParent()).add(array);
	}

	@Override
	public boolean visit(SimpleName node) {
		StructuralPropertyDescriptor prop = node.getLocationInParent();
		if(prop instanceof ChildPropertyDescriptor && ((ChildPropertyDescriptor) prop).getId().equals("name"))
			return true;

		if(node.getParent() instanceof MethodInvocation) {
			if(refTable.containsKey(node.getIdentifier()))
				argsTable.get(node.getParent()).add(refTable.get(node.getIdentifier()));
			else 
				throw new RuntimeException(node.getIdentifier() + " - Variable not found");
		}

		return true;
	}

	//	@Override
	//	public void endVisit(InfixExpression node) {
	//		if(node.getOperator().equals(InfixExpression.Operator.PLUS)) {
	//			System.out.println("PARENT:" + node.getParent().getClass());
	//			System.out.println("LEFT:" + node.getLeftOperand().getClass());
	//			System.out.println("RIGHT:" + node.getRightOperand().getClass());
	//			Object[] args = argsTable.get(node).toArray();
	//			int s = Integer.parseInt(args[0].toString()) + Integer.parseInt(args[1].toString());
	//			argsTable.get(node.getParent()).add(new Integer(s));
	//		}
	//
	//	}


	// timer
	private Object resolve(MethodInvocation node) {
		Object[] args = argsTable.get(node).toArray();
		try {
			Method method = match(node.getName().getFullyQualifiedName(), args);
			if(method != null && Modifier.isStatic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) {
				if(method.getReturnType().equals(void.class))
					throw new RuntimeException("Method does not return a value (void)");

				method.setAccessible(true);
				return method.invoke(null, args);
			}
		} 
		catch(InvocationTargetException ex) {
			throw new RuntimeException(ex.getCause().getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Static method not found");
	}



	private Method match(String methodName, Object[] args) {
		Method compatible = null;
		Method exact = null;

		for(Class<?> clazz : baseClasses.values()) {
			for(Method m : clazz.getDeclaredMethods()) {
				Class<?>[] types = m.getParameterTypes();

				if(m.getName().equals(methodName)) {
					if(exactMatch(types, args))
						exact = m;
					else if(compatible(types, args))
						compatible = compatible == null ? m : bestMatch(compatible, m, args);
				}
			}
		}

		return exact != null ? exact : compatible;
	}

	private static Constructor<?> match(Class<?> clazz, Object[] args) {
		for(Constructor<?> c : clazz.getDeclaredConstructors()) {
			Class<?>[] types = c.getParameterTypes();

			if(types.length == args.length) {
				boolean ok = true;

				for(int i = 0; ok && i < types.length; i++)
					if(!compatible(types[i], args[i]))
						ok = false;

				if(ok)
					return c;
			}
		}
		return null;

	}


	private static boolean compatible(Class<?>[] types, Object[] args) {
		if(types.length == args.length) {
			for(int i = 0; i < types.length; i++)
				if(!compatible(types[i], args[i]))
					return false;

			return true;
		}
		return false;
	}

	private static Method bestMatch(Method m1, Method m2, Object[] args) {
		Class<?>[] m1Types = m1.getParameterTypes();
		Class<?>[] m2Types = m2.getParameterTypes();
		int score = 0;
		for(int i = 0; i < m1Types.length; i++) {
			if(args[i] != null && args[i].getClass().equals(Integer.class)) {
				if(isPrimitiveIntegerType(m1Types[i]))
					score--;

				if(isPrimitiveIntegerType(m2Types[i]))
					score++;
			}
		}

		return score < 0 ? m1 : m2;
	}


	private static boolean exactMatch(Class<?>[] types, Object[] args) {
		if(types.length == args.length) {
			for(int i = 0; i < types.length; i++)
				if(args[i] != null && !types[i].equals(args[i].getClass()) || args[i] == null && !types[i].isPrimitive())
					return false;

			return true;
		}
		return false;
	}



	private static boolean compatible(Class<?> t, Object o) {
		return
				t.isPrimitive() && o != null && compatiblePrimitive(t, o.getClass()) ||
				!t.isPrimitive() && (o == null || t.isInstance(o));		
	}

	private static boolean isPrimitiveIntegerType(Class<?> type) {
		return type.equals(byte.class) || type.equals(short.class) || type.equals(int.class) || type.equals(long.class);
	}


	private static boolean compatiblePrimitive(Class<?> type, Class<?> objType) {
		return 
				isPrimitiveIntegerType(type) && objType.equals(Integer.class) ||
				type.equals(double.class) && objType.equals(Double.class) ||
				type.equals(char.class) && objType.equals(Character.class) ||
				type.equals(boolean.class) && objType.equals(Boolean.class) ||
				type.equals(double.class) && objType.equals(Integer.class) ||
				type.equals(char.class) && objType.equals(Integer.class);
	}




}
