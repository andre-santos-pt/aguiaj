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
package pt.org.aguiaj.extensibility;

public class TraceLocation {

	public final String fileName;
	public final String className;
	public final String methodName;
	public final int line;

	public TraceLocation(StackTraceElement element) {
		fileName = element.getClassName().replace('.', '/').concat(".java");
		className = element.getClassName();
		methodName = element.getMethodName();
		line = element.getLineNumber();
	}
	
	@Override
	public String toString() {
		return className + "." + methodName + "(...) : " + line;
	}

}
