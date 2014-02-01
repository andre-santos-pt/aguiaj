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

import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.Reference;

public interface Instruction {
	JavaCommand getCommand();
	boolean acceptText(
			String text, 
			Map<String, Reference> referenceTable, 
			Set<Class<?>> classSet) 
			throws ParseException;
}
