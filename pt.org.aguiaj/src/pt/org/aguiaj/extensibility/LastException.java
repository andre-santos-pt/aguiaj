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
package pt.org.aguiaj.extensibility;
public class LastException {
	public final String message;
	public final String fileName;
	public final int line;
	public final String[] args;

	public LastException(StackTraceElement element, String message, String[] args) {
		fileName = element.getClassName().replaceAll("\\.", System.getProperty("file.separator")).concat(".java");
		line = element.getLineNumber();
		this.message = message;
		this.args = args;
	}
	
	@Override
	public String toString() {
		return fileName + ":" + line;
	}
}