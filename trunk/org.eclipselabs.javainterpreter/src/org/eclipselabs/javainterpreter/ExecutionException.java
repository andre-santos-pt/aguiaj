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
package org.eclipselabs.javainterpreter;

public class ExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final int line;
	
	public ExecutionException(String message, int line) {
		super(message);
		this.line = line;
	}
	
	public int getLine() {
		return line;
	}
}
