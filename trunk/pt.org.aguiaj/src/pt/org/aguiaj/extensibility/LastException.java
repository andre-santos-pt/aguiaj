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
package pt.org.aguiaj.extensibility;
public class LastException {
	public final String message;
	public final String fileName;
	public final int line;
	public final String[] args;

	// TODO: review smell
	public LastException(StackTraceElement element, String message, String[] args) {
		String slash = System.getProperty("file.separator");
		if(slash.equals("\\"))
			slash = "\\\\";
		fileName = element.getClassName().replaceAll("\\.", slash).concat(".java");
		line = element.getLineNumber();
		this.message = message;
		this.args = args;
	}
	
	@Override
	public String toString() {
		return fileName + ":" + line;
	}
}
