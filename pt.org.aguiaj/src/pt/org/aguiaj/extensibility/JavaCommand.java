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
package pt.org.aguiaj.extensibility;

import pt.org.aguiaj.core.commands.Command;

public abstract class JavaCommand implements Command {
	
	public abstract String getReference();
	public abstract String getJavaInstruction();
		
	// TODO: review subclasses
	public boolean failed() {
		return false;
	}
	
	public String toString() {
		return getJavaInstruction();
	}	
}
