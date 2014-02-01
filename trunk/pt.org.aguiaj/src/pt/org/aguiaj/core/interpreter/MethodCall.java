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
package pt.org.aguiaj.core.interpreter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.extensibility.Reference;

public class MethodCall extends Expression implements Instruction {

	private Object targetObject;
	private Class<?> targetClass;
	private boolean staticInvocation;
	private Expression target;
	private Method method;
	private List<Expression> argsExp;
	private Object[] args;
	private String[] argsText;

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		int dotIndex = dotIndex(text);
		if(dotIndex == -1)
			return false;

		String left = text.substring(0, dotIndex).trim();
		String right = text.substring(dotIndex).trim();

		if(right.length() == 1)
			return false;
		else
			right = right.substring(1);

		if(right.indexOf('(') == -1 || right.indexOf(')') == -1)
			return false;

		targetClass = Common.findClass(classSet, left);

		staticInvocation = targetClass != null;

		if(targetClass == null) {
			target = ExpressionMatcher.match(left, referenceTable, classSet);
			if(target == null)
				throw new ParseException("Type/reference not found", text);

			targetClass = target.type();
		}
		String methodName = right.substring(0, right.indexOf('(')).trim();

		//		List<Method> allMethods = ClassModel.getInstance().getAllAvailableMethods(targetClass);
		boolean nameOk = false;
		for(Method m : targetClass.getMethods()) {
			boolean staticMethod = Modifier.isStatic(m.getModifiers());
			if(m.getName().equals(methodName) && (staticInvocation && staticMethod || !staticInvocation && !staticMethod))
				nameOk = true;
		}

		if(!nameOk)
			throw new ParseException("Method not found", targetClass.getName() + "." + methodName + "(...)");

		argsText = Common.getArgsText(right);
		if(argsText == null)
			return false;

		argsExp = Common.getExpressions(argsText, referenceTable, classSet);
		if(argsExp == null)
			return false;

		Class<?>[] argTypes = Common.getArgTypes(argsExp);

		method = findMethod(methodName, argTypes);

		return method != null;
	}

	private Method findMethod(String methodName, Class<?>[] argTypes) {
		//		for(Method m : ClassModel.getInstance().getAllAvailableMethods(targetClass)) {
		for(Method m : targetClass.getMethods()) {
			if(m.getName().equals(methodName) && m.getParameterTypes().length == argTypes.length) {
				boolean ok = true;
				for(int i = 0; i < argTypes.length && ok; i++) {
					if(argTypes[i] == null && m.getParameterTypes()[i].isPrimitive())
						ok = false;
					else if(argTypes[i] != null && !m.getParameterTypes()[i].isAssignableFrom(argTypes[i]))
						ok = false;
				}

				if(ok)
					return m;
			}
		}			

		throw new ParseException("Method not found", signature(methodName, argTypes));
	}

	private static String signature(String methodName, Class<?>[] argTypes) {
		String sig = methodName + "(";
		for(int i = 0; i < argTypes.length; i++) {
			if(i != 0)
				sig += ", ";
			sig += argTypes[i] == null ? "<ref>" : argTypes[i].getSimpleName();
		}
		return sig + ")";
	}

	private static int dotIndex(String text) {
		int brackets = 0;

		for(int i = text.length()-1; i >= 0; i--) {
			if(text.charAt(i) == '.' && brackets == 0)
				return i;
			else if(text.charAt(i) == ')')
				brackets++;
			else if(text.charAt(i) == '(')
				brackets--;			
		}

		return -1;


	}

	@Override
	public Class<?> type() {		
		return method.getReturnType();
	}


	@Override
	public Object resolve() {
		MethodInvocationCommand cmd = getCommand();
		ExceptionHandler.INSTANCE.execute(cmd);

		//		cmd.execute();
		return cmd.getResultingObject();
	}


	@Override
	public MethodInvocationCommand getCommand() {
		targetObject = target != null ? target.resolve() : null;	

		if(targetObject == null && !staticInvocation)
			throw new ParseException("Null pointer exception", target.getText());

		args = Common.resolveArgs(argsExp);

		return new MethodInvocationCommand(
				targetObject, 
				target != null ? target.getText() : targetClass.getSimpleName(), 
						method, 
						args, 
						argsText);
	}
}
