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

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;

public class StringLiteral extends Literal implements Instruction {
	
	protected StringLiteral() {
		super(String.class);
	}

	public final static String stringRegex = "\"[^\"]*\"";
	
	@Override
	public boolean accept(String text) {		
		if(text.matches(stringRegex)) {
			value = text.substring(1, text.length()-1);
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		if(text.matches(stringRegex)) {
			value = text.substring(1, text.length()-1);
			for(Reference ref : referenceTable.values()) {
				if(value.equals(ref.object))
					value = ref.object;
			}
			return true;
		}
		else
			return false;
	}
	
	@Override
	public JavaCommand getCommand() {
		return new NewReferenceCommand(String.class, value, "\"" + value + "\""); 				
	}
	
}
