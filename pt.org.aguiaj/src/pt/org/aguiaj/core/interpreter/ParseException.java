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

public class ParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public final String cause;
	public final String detail;
	
	public ParseException(String cause, String detail) {
		super(cause + ": " + detail);
		this.cause = cause;
		this.detail = detail;
	}
}
