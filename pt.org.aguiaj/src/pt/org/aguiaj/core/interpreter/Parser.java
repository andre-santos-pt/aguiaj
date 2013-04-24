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

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.extensibility.Reference;
import pt.org.aguiaj.objects.ObjectModel;

public class Parser {
	
	public static Instruction accept(String instruction) {
		return accept(instruction, 
				ObjectModel.getInstance().getReferenceTable(),
				ClassModel.getInstance().getAllClasses());
	}
	
	public static Instruction accept(
			String instruction, 
			Map<String, Reference> referenceTable,
			Set<Class<?>> classSet) {
		
		String instructionTrim = instruction.trim();
		if(instructionTrim.endsWith(";"))
			instructionTrim = instructionTrim.substring(0, instructionTrim.length()-1);
		
		return InstructionMatcher.match(instructionTrim, referenceTable, classSet);
	}
}
