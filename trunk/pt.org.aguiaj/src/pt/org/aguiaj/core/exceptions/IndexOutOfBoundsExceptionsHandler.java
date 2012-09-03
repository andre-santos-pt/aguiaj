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
package pt.org.aguiaj.core.exceptions;

import pt.org.aguiaj.common.PluggableExceptionHandler;

@PluggableExceptionHandler(IndexOutOfBoundsException.class)
public class IndexOutOfBoundsExceptionsHandler implements SpecificExceptionHandler {

	public String getMessage(Throwable exception) {		
		int line = exception.getStackTrace()[0].getLineNumber();
		String className = exception.getStackTrace()[0].getClassName();
		return "On line " + line + " of class " + className +
		" an invalid index was used (" + exception.getMessage() + ").";
	}
}
