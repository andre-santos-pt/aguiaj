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

@PluggableExceptionHandler(ArithmeticException.class)
public class ArithmeticExceptionHandler implements SpecificExceptionHandler {
	
	public String getMessage(Throwable exception) {
		String message = exception.getMessage();
		int line = exception.getStackTrace()[0].getLineNumber();
		String className = exception.getStackTrace()[0].getClassName();
		
		if("/ by zero".equals(message))
			message = "On line " + line + " of class " + className +
			" there was an illegal division by zero.";
				
		return message;
	}
}
