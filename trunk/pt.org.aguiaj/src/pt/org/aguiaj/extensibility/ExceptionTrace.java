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

import java.util.ArrayList;
import java.util.List;

import pt.org.aguiaj.classes.ClassModel;

public class ExceptionTrace {
	private String message;
	
	private final String[] args;
	private List<TraceLocation> trace;
	private int next;
	
	public ExceptionTrace(Throwable exception, String message, String[] args) {
		this.args = args;
		this.message = message;
		next = -1;
		trace = new ArrayList<TraceLocation>();
		for(StackTraceElement e : exception.getStackTrace()) {
			String className = e.getClassName();
			
			if(ClassModel.getInstance().isUserClass(className)) {
				trace.add(new TraceLocation(e));
				if(message == null)
					message += "\n(line " +  e.getLineNumber() + ")";
			}
		}
	}
	
	public String getMessage() {
		return message;
	}

	public TraceLocation getLocation() {
		return trace.get(next);
	}
	
	public void moveFrontwards() {		
		if(next > 0)
			next--;
	}
	
	public void moveBackwards() {
		if(next + 1 < trace.size())
			next++;
	}
	
	
	@Override
	public String toString() {
		return trace.get(0).toString();
	}
}
