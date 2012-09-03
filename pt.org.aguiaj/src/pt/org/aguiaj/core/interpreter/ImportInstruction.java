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

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.ImportCommand;
import pt.org.aguiaj.core.commands.java.JavaCommand;

public class ImportInstruction implements Instruction {

	private String packageName;
	
	@Override
	public JavaCommand getCommand() {
		return new ImportCommand(packageName);
	}

	@Override
	public boolean acceptText(String text,
			Map<String, Reference> referenceTable, Set<Class<?>> classSet)
			throws ParseException {
		
		String s = text.trim();
		if(!s.startsWith("import"))
			return false;
		
		packageName = s.substring("import".length()).trim();
		
		return true;
	}

}
