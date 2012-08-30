/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.core.interpreter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.ArrayObjectCreationCommand;
import pt.org.aguiaj.core.commands.java.JavaCommandWithReturn;

public class ArrayCreation extends Expression implements Instruction {
	private Class<?> type;
	private List<Expression> indexExpressions;

	@Override
	protected boolean accept(String text,
			Map<String, Reference> referenceTable, Set<Class<?>> classSet)
	throws ParseException {

		if(!text.startsWith("new"))
			return false;

		String classAndArgs = text.substring("new".length()).trim();

		int i = classAndArgs.indexOf('[');
		if(i == -1)
			return false;

		String typeName = classAndArgs.substring(0, i).trim();

		String pos = classAndArgs.substring(i);

		if(!pos.matches(Common.arrayPositionRegex))
			return false;

		String[] indexesParts = Common.parts(pos);

		indexExpressions = new ArrayList<Expression>();

		for(String part : indexesParts) {
			String expText = part.substring(1, part.length()-1).trim();
			if(expText.isEmpty()) {
				// first index has to be defined
				if(indexExpressions.isEmpty())
					return false;
				
				indexExpressions.add(null);
			}
			else {			
				// TODO : bug? e.g. new int[3][]
				// once one index is null, all the following have to be
				if(!indexExpressions.isEmpty() && indexExpressions.get(indexExpressions.size()-1) == null)
					return false;
				
				Expression exp = ExpressionMatcher.match(expText, referenceTable, classSet);

				if(exp == null)
					throw new ParseException("Invalid expression", expText);

				indexExpressions.add(exp);
			}
		}								

		type = Common.findClass(classSet, typeName);
		if(type == null)
			type = Common.getPrimitiveType(typeName);

		if(type == null)
			throw new ParseException("Type not found", typeName);

		return true;
	}

	@Override
	public Class<?> type() {
		return Array.newInstance(componentType(), new int[indexExpressions.size()]).getClass();
	}

	private Class<?> componentType() {
		return type;
	}

	@Override
	public Object resolve() {
		JavaCommandWithReturn cmd = getCommand();
		cmd.setSilent();
		cmd.execute();
		return cmd.getResultingObject();
	}

	@Override
	public ArrayObjectCreationCommand getCommand() {		
		return new ArrayObjectCreationCommand(componentType(), Common.resolveIndexes(indexExpressions, null));
	}

}
