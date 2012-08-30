/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.core.exceptions;

import pt.org.aguiaj.common.PluggableExceptionHandler;

@PluggableExceptionHandler(ArrayIndexOutOfBoundsException.class)
public class ArrayIndexOutOfBoundsExceptionsHandler implements SpecificExceptionHandler {

	public String getMessage(Throwable exception) {
		int line = exception.getStackTrace()[0].getLineNumber();
		String className = exception.getStackTrace()[0].getClassName();
		
		return "On line " + line + " of class " + className +
		" there was an illegal access to the array position " + exception.getMessage() + ".";
	}

}
