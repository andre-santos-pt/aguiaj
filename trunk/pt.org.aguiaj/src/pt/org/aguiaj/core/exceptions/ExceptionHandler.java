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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.PluggableExceptionHandler;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.extensibility.LastException;
import pt.org.aguiaj.standard.StandardNamePolicy;

public enum ExceptionHandler {
	INSTANCE;
	
	private List<SpecificExceptionHandler> handlers;
	private Set<Method> previousMethodErrors;
	
	private LastException lastException;
	private String[] lastArgs;
	
	private ExceptionHandler() {
		handlers = new ArrayList<SpecificExceptionHandler>();
		previousMethodErrors = new HashSet<Method>();
	}

	public void addHandler(SpecificExceptionHandler handler) {
		handlers.add(handler);		
	}

	public LastException getLastException() {		
		return lastException;
	}
	
	public String[] getLastArgs() {
		return lastArgs;
	}
	
	public void clearErrors() {
		previousMethodErrors.clear();
	}

	public synchronized void handleException(Method method, String[] args, Throwable exception) {
//		if(method != null) {
//			if(previousMethodErrors.contains(method)) {
//				return;
//			}			
//		}
			
		String message = exception.getMessage();
		if(message == null)
			message = "";
		
		String title = StandardNamePolicy.prettyClassName(exception.getClass());
		int icon = SWT.ICON_ERROR;

		if(exception instanceof IllegalArgumentException || 
				exception instanceof IllegalStateException ||
				exception instanceof NullPointerException && exception.getMessage() != null) {
			icon = SWT.ICON_WARNING;	
		}
		else if(exception instanceof StackOverflowError) {
			title = UIText.STACK_OVERFLOW.get();
			String methodText = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(..)";
			message = UIText.CHECK_METHOD_RECURSION.get(methodText);
			if(method != null)
				previousMethodErrors.add(method);
		}
		else if(exception instanceof OutOfMemoryError) {
			title = UIText.OUT_OF_MEMORY.get();
			String methodText = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "(..)";
			message = "Check method " + methodText + ".";
			if(method != null)
				previousMethodErrors.add(method);
		}
		else if(exception instanceof Error) {
			title = UIText.COMPILATION_ERRORS.get();
			int line = exception.getStackTrace()[0].getLineNumber();
			String className = exception.getStackTrace()[0].getClassName();
			message = UIText.CHECK_CLASS_AT.get(className, line, exception.getMessage());
			if(method != null)
				previousMethodErrors.add(method);
		}
		else {
			for(SpecificExceptionHandler handler : handlers)
				if(handler.getClass().getAnnotation(PluggableExceptionHandler.class).value().equals(exception.getClass()))
					message = handler.getMessage(exception);
		}
		
		for(StackTraceElement traceElement : exception.getStackTrace()) {
			String className = traceElement.getClassName();
			
			if(ClassModel.getInstance().isUserClass(className)) {
				lastException = new LastException(traceElement, message, args);
				message += "\n(line " +  lastException.line + ")";
				break;
			}
		}
		
		SWTUtils.showMessage(title, message, icon);
	}
}
