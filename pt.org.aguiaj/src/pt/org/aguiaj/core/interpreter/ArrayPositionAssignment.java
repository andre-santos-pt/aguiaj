/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.core.interpreter;

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.core.commands.java.ArrayPositionAssignmentCommand;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.Reference;

public class ArrayPositionAssignment extends Assignment {
	private ArrayPosition position;
	
	@Override
	public boolean accept(String left, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		ArrayPosition arrayPos = new ArrayPosition();
		if(!arrayPos.acceptText(left, referenceTable, classSet))
			return false;
		
		position = arrayPos;
		return true;
	}
	
	@Override
	public JavaCommand getCommand() {
		Object arrayObject = position.arrayObject();
		return new ArrayPositionAssignmentCommand(
				arrayObject, 
				position.getIdentifier(),
				position.resolveIndexes(arrayObject), 
				getExpression().resolve(), 
				getExpression().getText());
	}

	@Override
	public Assignable getAssignable() {
		return position;
	}
}
