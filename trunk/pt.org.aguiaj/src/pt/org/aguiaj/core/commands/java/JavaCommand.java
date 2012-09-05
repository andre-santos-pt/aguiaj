/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.commands.java;

import pt.org.aguiaj.core.commands.Command;

public abstract class JavaCommand implements Command {
	
	public abstract String getJavaInstruction();
		
	public boolean failed() {
		return false;
	}
	
	public String toString() {
		return getJavaInstruction();
	}	
}