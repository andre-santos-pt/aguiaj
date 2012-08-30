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
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;

public class ReferenceDeclarationAndAssignment extends Assignment {
	private ReferenceDeclaration refDec;
	
	@Override
	public boolean accept(String left, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		refDec = new ReferenceDeclaration();
		if(!refDec.acceptText(left, referenceTable, classSet))
			return false;
		
		return true;				
	}

	@Override
	public JavaCommand getCommand() {
		return new NewReferenceCommand(
				refDec.type(), 
				getExpression().resolve(), 
				getExpression().getText(), 
				refDec.id());
	}

	@Override
	public Assignable getAssignable() {
		return refDec;
	}



}
