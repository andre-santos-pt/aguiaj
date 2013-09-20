package org.eclipselabs.javainterpreter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
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
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipselabs.reflectionutils.invocation.MethodInvocationThread;


class ExpressionVisitor extends ASTVisitor {

	private Context context;

	private Map<Expression, List<Object>> argsTable;
	private Map<MethodInvocation, Object> methodTarget;

	private Object result;


	public ExpressionVisitor(Context context) {
		this.context = context;
		argsTable = new HashMap<Expression, List<Object>>();
		methodTarget = new HashMap<MethodInvocation, Object>();
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

	public boolean visit(InfixExpression e) {
		throw new RuntimeException("not supported: " + e);
	}

	//	public boolean visit(PrefixExpression e) {
	//		throw new RuntimeException("not supported: " + e);
	//	}

	public boolean visit(PostfixExpression e) {
		throw new RuntimeException("not supported: " + e);
	}

	public boolean visit(ParenthesizedExpression e) {
		throw new RuntimeException("not supported: " + e);
	}


	@Override
	public void endVisit(Assignment node) {
		result = argsTable.get(node).get(0);

		context.addReference(result == null ? Object.class : result.getClass(), node.getLeftHandSide().toString(), result);
	}


	@Override
	public void endVisit(ClassInstanceCreation node) {
		Object r = resolve(node);
		if(isMethodTarget(node)) {
			methodTarget.put((MethodInvocation) node.getParent(), r);
		}
		else if(node.getParent() instanceof ExpressionStatement) {
			result = r;
		}
		else { 
			argsTable.get(node.getParent()).add(r);	
		}
	}

	private Object resolve(ClassInstanceCreation node) {
		Type type = (Type) node.getStructuralProperty(ClassInstanceCreation.TYPE_PROPERTY);
		Class<?> clazz = loadClass(type);
		Object[] args = argsTable.get(node).toArray();
		Constructor<?> c = match(clazz, args);
		if(c != null) {
			try {
				c.setAccessible(true);
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
		else if(type.getDimensions() == args.length){
			int[] dims = new int[type.getDimensions()];
			for(int i = 0; i < dims.length; i++)
				dims[i] = (int) Double.parseDouble(args[i].toString());

			Type t = (Type) type.getStructuralProperty(ArrayType.COMPONENT_TYPE_PROPERTY);

			Class<?> clazz = loadClass(t);
			array = Array.newInstance(clazz, dims);
		}
		else
			throw new RuntimeException("Invalid array creation");

		if(node.getParent() instanceof ExpressionStatement)
			result = array;
		else
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
		String className = t.toString();

		if(context.isClassAvailable(className)){
			return context.getClass(className);
		}
		else if(t.isPrimitiveType()) {
			return getPrimitiveType(className);
		}
		else if(t.isArrayType()) {
			for(Class<?> primitive : primitiveTypes)
				if(t.toString().startsWith(primitive.getSimpleName()))
					return primitive;
		}
		else {
			try {
				return  Class.forName(className);
			} catch (ClassNotFoundException e) {
				try {
					return Class.forName("java.lang." + className);
				} catch (ClassNotFoundException e1) {

				}

			}
		}
		throw new IllegalArgumentException("Could not load type - " + className);
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
		ASTNode parent = node.getParent();
		if(parent == null)
			result = node.getLiteralValue();
		else if(isMethodTarget(node))
			methodTarget.put((MethodInvocation) parent, node.getLiteralValue());
		else
			argsTable.get(parent).add(node.getLiteralValue());

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
		argsTable.put(node, new ArrayList<Object>());
		return true;
	}

	@Override
	public void endVisit(ArrayInitializer node) {
		ArrayType type = (ArrayType) node.getParent().getStructuralProperty(ArrayCreation.TYPE_PROPERTY);

		Type t = (Type) type.getStructuralProperty(ArrayType.COMPONENT_TYPE_PROPERTY);
		Class<?> clazz = loadClass(t);

		Object array = Array.newInstance(clazz, argsTable.get(node).size());
		for(int i = 0; i < argsTable.get(node).size(); i++) {
			Array.set(array, i, argsTable.get(node).get(i));
		}
		argsTable.get(node.getParent()).add(array);
	}

	@Override
	public boolean visit(SimpleName node) {
		if(isMethodName(node))
			return true;

		if(node.getParent() instanceof MethodInvocation && context.existsReference(node.getIdentifier())) {
			Object target = context.getObject(node.getIdentifier());

			MethodInvocation method = (MethodInvocation) node.getParent();

			if(target == null)
				throw new RuntimeException(node.getIdentifier() + " - Variable not found");

			if(isMethodTarget(node)) {
				methodTarget.put(method, target);
			}
			else {
				argsTable.get(method).add(target);
			}
		}

		return true;
	}

	private Object resolve(MethodInvocation node) {
		Object[] args = argsTable.get(node).toArray();

		Object target = methodTarget.get(node);

		if(methodTarget.containsKey(node) && target == null)
			throw new RuntimeException("Null pointer exception");

		Expression exp = node.getExpression();

		String className = methodTarget.containsKey(node) ? 
				target.getClass().getSimpleName() : exp == null ? null : exp.toString();

				Method method = null;
				try {
					method = match(node.getName().getFullyQualifiedName(), args, className);
				}
				catch(java.lang.NoClassDefFoundError e) {
					
				}
				
				if(method != null && !Modifier.isAbstract(method.getModifiers())) {

					if(method.getReturnType().equals(void.class))
						throw new RuntimeException("Method does not return a value (void)");

					if(target == null && !Modifier.isStatic(method.getModifiers()))
						throw new RuntimeException("Method is not static");

					MethodInvocationThread thread = new MethodInvocationThread(method, target, args);
					thread.execute(1000);

					if(thread.hasFailed()) {
						Throwable exc = thread.getException();
						String loc = exc.getStackTrace()[0].getFileName() + ": " + exc.getStackTrace()[0].getLineNumber();

						throw new ExecutionException(exc.getClass().getSimpleName() + " at " + loc, exc.getStackTrace()[0].getLineNumber());
					}
					else if(thread.timeoutReached())
						throw new RuntimeException("Infinite cycle?");
					else
						return thread.getResultingObject();

				}
				else throw new RuntimeException("Method not found");

	}


	private static boolean isMethodTarget(ASTNode node) {
		StructuralPropertyDescriptor prop = node.getLocationInParent();
		return 
				node.getParent() instanceof MethodInvocation && 
				prop instanceof ChildPropertyDescriptor && 
				((ChildPropertyDescriptor) prop).getId().equals("expression");
	}

	private static boolean isMethodName(ASTNode node) {
		StructuralPropertyDescriptor prop = node.getLocationInParent();
		return 
				node.getParent() instanceof MethodInvocation && 
				prop instanceof ChildPropertyDescriptor && 
				((ChildPropertyDescriptor) prop).getId().equals("name");
	}


	private Method match(String methodName, Object[] args, String className) {
		if(className != null && context.isClassAvailable(className)) {
			Class<?> clazz = context.getClass(className);
			return matchMethod(clazz, methodName, args);
		}
		else {
			Method method = null;
			for(Class<?> c : context.getImplicitClasses()) {
				Method m =  matchMethod(c, methodName, args);
				method = method == null ? m : bestMatch(method, m, args);
			}
			return method;
		}
	}


	private static Method matchMethod(Class<?> clazz, String methodName, Object[] args) {
		Method compatible = null;
		for(Method m : clazz.getDeclaredMethods()) {
			Class<?>[] types = m.getParameterTypes();

			if(m.getName().equals(methodName)) {
				if(exactMatch(types, args))
					return m;
				else if(compatible(types, args))
					compatible = compatible == null ? m : bestMatch(compatible, m, args);
			}
		}
		return compatible;
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
