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
package pt.org.aguiaj.core.commands.java;


public abstract class JavaCommandWithReturn extends JavaCommand {
	private boolean silent = false;
	
	public abstract String getReference();
	public abstract Class<?> getReferenceType();
	public abstract Object getResultingObject();
	
	public final boolean isSilent() {
		return silent;
	}
	
	public final void setSilent() {
		silent = true;
	}
	
}
