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

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.core.commands.java.AttributeAssignmentCommand;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.Reference;

public class AttributeAssignment extends Assignment {
	private Attribute attribute;
	
	@Override
	public boolean accept(String left, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		attribute = new Attribute();
		if(!attribute.acceptText(left, referenceTable, classSet))
			return false;
		
		return true;
	}

	@Override
	public JavaCommand getCommand() {
		return new AttributeAssignmentCommand(
				attribute.getOwnerObject(),
				attribute.field(), 
				getExpression().resolve(), 
				getExpression().getText());
	}

	@Override
	public Assignable getAssignable() {
		return attribute;
	}
	
}
