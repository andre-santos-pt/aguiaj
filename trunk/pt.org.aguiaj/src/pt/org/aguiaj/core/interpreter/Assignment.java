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

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;


public abstract class Assignment implements Instruction {
	private Expression expression;

	public boolean acceptText(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		String[] parts = text.split("=");
		if(parts.length != 2)
			return false;
		
		String left = parts[0].trim();
		String right = parts[1].trim();

		if(!accept(left, referenceTable, classSet))
			return false;
			
		expression = ExpressionMatcher.match(right, referenceTable, classSet);
		if(expression == null)
			throw new ParseException("Invalid expression", right);
		
		if(!getAssignable().canAssign())
			throw new ParseException("Cannot be assigned", left);
		
		Common.checkAssignmentTypes(getAssignable(), expression);
		
		return true;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public abstract Assignable getAssignable();
	
	public abstract boolean accept(String left, Map<String, Reference> referenceTable, Set<Class<?>> classSet);
}
