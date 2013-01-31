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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pt.org.aguiaj.classes.ClassModel;

public class ExceptionTrace {
	private String message;
	
	private final String[] args;
	private List<TraceLocation> trace;
	
	public ExceptionTrace(Throwable exception, String message, String[] args) {
		this.args = args;
		this.message = message;
		trace = new ArrayList<TraceLocation>();
		for(StackTraceElement e : exception.getStackTrace()) {
			String className = e.getClassName();
			
			if(ClassModel.getInstance().isUserClass(className)) {
				trace.add(0, new TraceLocation(e));
				if(message == null)
					message += "\n(line " +  e.getLineNumber() + ")";
			}
		}
	}
	
	public List<TraceLocation> getTrace() {
		return Collections.unmodifiableList(trace);
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return trace.get(0).toString();
	}
}
