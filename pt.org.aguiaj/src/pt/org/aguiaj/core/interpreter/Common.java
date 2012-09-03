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
package pt.org.aguiaj.core.interpreter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.ArrayObjectCreationCommand;
import pt.org.aguiaj.core.commands.java.ConstructorInvocationCommand;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;

public class Common {
	public final static String integerRegex = "\\-?\\d+";
	public final static String positiveIntRegex = "\\d+";
	public final static String realRegex = integerRegex + "\\." + positiveIntRegex;

	public static String arrayPositionRegex = "\\[\\s*" + "(.)+" + "\\s*\\]";
	public static String arrayBracketsRegex = "(\\[\\s*\\])+";

	public final static boolean isValidJavaIdentifier(String s)
	{
		if(s.matches(javaKeyword))
			return false;

		// an empty or null string cannot be a valid identifier
		if (s == null || s.length() == 0)
		{
			return false;
		}

		char[] c = s.toCharArray();
		if (!Character.isJavaIdentifierStart(c[0]))
		{
			return false;
		}

		for (int i = 1; i < c.length; i++)
		{
			if (!Character.isJavaIdentifierPart(c[i]))
			{
				return false;
			}
		}

		return true;
	}



	public static String or(String... exp) {
		String result = "";
		for(int i = 0; i < exp.length; i++) {
			if(i != 0)
				result += "|";
			result += exp[i];
		}
		return result;
	}


	public final static String javaKeyword =
		or("abstract", "continue", "for", "new", "switch", "assert", "default",
				"goto",	"package", "synchronized", "boolean", "do", "if", "private",
				"this", "break", "double",	"implements", "protected", "throw",
				"byte",	"else",	"import",	"public",	"throws", "case", "enum",
				"instanceof", "return",	"transient", "catch", "extends", "int",	
				"short", "try", "char",	"final", "interface", "static",	"void",
				"class", "finally",	"long",	"strictfp",	"volatile", "const", "float",
				"native", "super",	"while");


	public static String[] getArgsText(String text) {
		int i = text.indexOf('(');
		int j = closingBrackets(text, 0);

		if(i == -1 || j == text.length())
			return null;

		String args = text.substring(i+1, j).trim();

		if(args.equals(""))
			return new String[0];

		List<Integer> breaks = new ArrayList<Integer>();
		int brackets = 0;

		for(int x = 0; x < args.length(); x++) {
			if(args.charAt(x) == '(')
				brackets++;
			else if(args.charAt(x) == ')')
				brackets--;
			else if(args.charAt(x) == ',' && brackets == 0)
				breaks.add(x);
		}

		String[] argsArray = new String[breaks.size() + 1];
		int start = 0;
		for(int x = 0; x < argsArray.length; x++) {
			if(x == breaks.size()) {
				argsArray[x] = args.substring(start).trim();
			}
			else {
				int end = breaks.get(x);
				argsArray[x] = args.substring(start, end).trim();
				start = end + 1;
			}
		}
		return argsArray;
	}



	public static int closingBrackets(String text, int j) {
		int brackets = 0;
		for(; j< text.length(); j++) {
			if(text.charAt(j) == ')' && brackets == 1)
				break;
			else if(text.charAt(j) == ')')
				brackets--;
			else if(text.charAt(j) == '(')
				brackets++;			
		}
		return j;
	}

	public static List<Expression> getExpressions(String[] argsText, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		List<Expression> argsExp = new ArrayList<Expression>(); 
		for(String arg : argsText) {
			Expression exp = ExpressionMatcher.match(arg, referenceTable, classSet);
			if(exp == null)
				throw new ParseException("Invalid expression", arg);

			argsExp.add(exp);
		}

		return argsExp;
	}

	public static Class<?>[] getArgTypes(List<Expression> argsExp) {
		Class<?>[] argTypes = new Class<?>[argsExp.size()];
		for(int x = 0; x < argTypes.length; x++)
			argTypes[x] = argsExp.get(x).type();

		return argTypes;
	}


	public static Object[] resolveArgs(List<Expression> argsExp) {
		Object[] args = new Object[argsExp.size()];
		for(int i = 0; i < args.length; i++) {
			Expression argExp = argsExp.get(i);

			if(argExp instanceof MethodCall) {
				MethodInvocationCommand cmd = ((MethodCall) argExp).getCommand();
				cmd.setSilent();
				cmd.execute();
				cmd.waitToFinish();
				args[i] = cmd.getResultingObject();
			}
			else if(argExp instanceof ConstructorCall) {
				ConstructorInvocationCommand cmd = ((ConstructorCall) argExp).getCommand();
				cmd.setSilent();
				cmd.execute();
				cmd.waitToFinish();
				args[i] = cmd.getResultingObject();				
			}	
			else {
				args[i] = argExp.resolve();
			}
		}
		return args;
	}

	public static Class<?> findClass(Set<Class<?>> classSet, String name) {
		for(Class<?> c : classSet)
			if(c.getSimpleName().equals(name))
				return c;

		return null;
	}

	public static int countIndexes(String text) {
		int indexes = 0;
		boolean bracketsOn = false;
		for(char c : text.toCharArray()) {
			if(c == '[')
				bracketsOn = true;
			else if(c == ']' && bracketsOn) {
				indexes++;
				bracketsOn = false;
			}
		}
		return indexes;			
	}

	private static int[] breaks(String text) {
		int[] ret = new int[countIndexes(text)];
		int i = 0;
		boolean bracketsOn = false;
		for(int j = 0; j < text.length(); j++) {
			if(text.charAt(j) == '[')
				bracketsOn = true;
			else if(text.charAt(j) == ']' && bracketsOn && i != ret.length) {
				ret[i++] = j+1;
				bracketsOn = false;
			}
		}
		return ret;
	}

	public static String[] parts(String text) {
		int[] breaks = breaks(text);
		String[] ret = new String[breaks.length];

		for(int i = 0; i < breaks.length; i++) {
			if(i == 0)
				ret[i] = text.substring(0, breaks[i]);
			else
				ret[i] = text.substring(breaks[i-1], breaks[i]);			
		}

		return ret;
	}

	public static int[] resolveIndexes(List<Expression> indexExpressions, Object arrayObject) {
		Object array = arrayObject;
		int[] indexes = new int[indexExpressions.size()];
		int i = 0;
		for(Expression exp : indexExpressions) {
			if(exp == null) {
				indexes[i] = ArrayObjectCreationCommand.UNDEFINED;
			}
			else {
				Object e = exp.resolve();
				if(!(e instanceof Integer))
					throw new ParseException("Not an integer", exp.getText());
				else if(((Integer) e).intValue() < 0)
					throw new ParseException("Not a positive integer", exp.getText());

				indexes[i] = ((Integer) e).intValue();

				if(array != null) {
					if(!array.getClass().isArray())
						throw new ParseException("Too many dimensions", indexExpressions.size() + "");

					if(indexes[i] >= Array.getLength(array))
						throw new ParseException("Array index out of bounds", exp.getText() + " resolved to " + indexes[i]);

					array = Array.get(array, indexes[i]);
				}
			}
			i++;
		}
		return indexes;
	}

	public static void checkAssignmentTypes(Assignable assignable, Expression expression) {
		if(!(expression instanceof Null || assignable.type().isAssignableFrom(expression.type())))
			throw new ParseException("Incompatible types", 
					assignable.getExpressionText() + " (type " + assignable.type().getSimpleName() + ") and " + 
					expression.getText() + " (type " + expression.type().getSimpleName() + ")");
	}


	public static Class<?> getPrimitiveType(String text) {
		if(text.equals(int.class.getSimpleName()))
			return int.class;
		else if(text.equals(double.class.getSimpleName()))
			return double.class;
		else if(text.equals(char.class.getSimpleName()))
			return char.class;
		else if(text.equals(boolean.class.getSimpleName()))
			return boolean.class;

		return null;
	}
}
