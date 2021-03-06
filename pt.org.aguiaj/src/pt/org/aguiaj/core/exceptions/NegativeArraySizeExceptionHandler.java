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
package pt.org.aguiaj.core.exceptions;

import pt.org.aguiaj.common.PluggableExceptionHandler;

@PluggableExceptionHandler(NegativeArraySizeException.class)
public class NegativeArraySizeExceptionHandler implements SpecificExceptionHandler {

	
	public String getMessage(Throwable exception) {
		int line = exception.getStackTrace()[0].getLineNumber();
		String className = exception.getStackTrace()[0].getClassName();
		
		return "On line " + line + " of class " + className +
		" a negative number was incorrectly given as the array size.";
	}

}
