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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.core.commands.java.ConstructorInvocationCommand;

public class ConstructorCall extends Expression implements Instruction {

	private Class<?> clazz;
	private Constructor<?> constructor;
	private List<Expression> argsExp;
	private String[] argsText;
	private Object[] args;

	@Override
	public boolean accept(
			String text, 
			Map<String, pt.org.aguiaj.extensibility.Reference> referenceTable, 
			Set<Class<?>> classSet) throws ParseException {

		if(!text.startsWith("new"))
			return false;

		int j = Common.closingBrackets(text, 0);

		if(j == text.length())
			return false;

		String classAndArgs = text.substring("new".length(), j+1).trim();

		argsText = Common.getArgsText(classAndArgs);
		if(argsText == null)
			return false;

		String className = classAndArgs.substring(0, classAndArgs.indexOf('(')).trim();

		argsExp = Common.getExpressions(argsText, referenceTable, classSet);
		if(argsExp == null)
			return false;

		clazz = Common.findClass(classSet, className);
		if(clazz == null)
			throw new ParseException("Class not found", className);
		
		if(Modifier.isAbstract(clazz.getModifiers()))
			throw new ParseException("Type is abstract", className);
		
		Class<?>[] argTypes = Common.getArgTypes(argsExp);
		
		constructor = findConstructor(argTypes);

		return constructor != null;
	}
	
	private Constructor<?> findConstructor(Class<?>[] argTypes) {	
		for(Constructor<?> c : ClassModel.getInstance().getVisibleConstructors(clazz)) {
			if(c.getParameterTypes().length == argTypes.length) {
				boolean ok = true;
				for(int i = 0; i < argTypes.length && ok; i++) {
					if(argTypes[i] == null && c.getParameterTypes()[i].isPrimitive())
						ok = false;
					else if(argTypes[i] != null && !c.getParameterTypes()[i].isAssignableFrom(argTypes[i]))
						ok = false;
				}

				if(ok)
					return c;
			}
		}			

		throw new ParseException("Constructor not found", clazz.getSimpleName() + "(" + argTypesText(argTypes) + ")");
	}

	private static String argTypesText(Class<?>[] args) {
		String ret = "";
		for(Class<?> c : args) {
			if(!ret.isEmpty())
				ret += ", ";
			ret += c == null ? "<ref>" : c.getSimpleName();
		}		
		return ret;
	}
	
	@Override
	public Class<?> type() {
		return clazz;
	}

	@Override
	public Object resolve() throws ParseException {
		ConstructorInvocationCommand cmd = getCommand();
		cmd.execute();
		return cmd.getResultingObject();
	}

	@Override
	public ConstructorInvocationCommand getCommand() {		
		args = Common.resolveArgs(argsExp);
		
		return new ConstructorInvocationCommand(constructor, args);
	}

}
