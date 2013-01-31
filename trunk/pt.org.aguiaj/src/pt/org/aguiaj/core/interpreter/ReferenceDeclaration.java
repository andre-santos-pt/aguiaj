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

import java.lang.reflect.Array;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;

public class ReferenceDeclaration implements Instruction, Assignable {
	private ExistingReference existingReference;

	@Override
	public boolean acceptText(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		int nArrayIndexes = 0;
		String tmp = text.trim();		

		String typeName = null;
		String varName = null;
		Class<?> clazz = null;
		
		if(tmp.matches("[\\S]+\\s*" + Common.arrayBracketsRegex + "\\s*[\\S]+")) {
			typeName = tmp.substring(0, tmp.indexOf('[')).trim();
			String brackets = tmp.substring(tmp.indexOf('['), tmp.lastIndexOf(']') + 1);
			nArrayIndexes = Common.countIndexes(brackets);
			varName = tmp.substring(tmp.lastIndexOf(']') + 1).trim();
			clazz = Common.findClass(classSet, typeName);
			if(clazz == null)
				clazz = Common.getPrimitiveType(typeName);			
		}
		else {
			if(tmp.split("\\s+").length != 2)
				return false;

			int i = tmp.indexOf(' ');

			typeName = tmp.substring(0, i).trim();
			varName = tmp.substring(i).trim();	
			
			if(!Common.isValidJavaIdentifier(varName))
				return false;
			
			clazz = Common.findClass(classSet, typeName);
		}

		if(clazz == null)
			throw new ParseException("Type not found", typeName);

		if(nArrayIndexes != 0)
			clazz = Array.newInstance(clazz, new int[nArrayIndexes]).getClass();

		if(referenceTable.containsKey(varName))
			throw new ParseException("Reference already exists", text);

		if(!Common.isValidJavaIdentifier(varName))
			throw new ParseException("Invalid identifier", text);
		
		existingReference = new ExistingReference(varName, clazz, null);
		
		return true;
	}

	public Class<?> type() {
		return existingReference.type();
	}

	public String id() {
		return existingReference.getIdentifier();
	}

	@Override
	public JavaCommand getCommand() {
		return new NewReferenceCommand(
				existingReference.type(), 
				null, 
				null, 
				existingReference.getIdentifier());
	}

	@Override
	public String getExpressionText() {
		return existingReference.getExpressionText();
	}

	@Override
	public boolean canAssign() {
		return existingReference.canAssign();
	}

}
