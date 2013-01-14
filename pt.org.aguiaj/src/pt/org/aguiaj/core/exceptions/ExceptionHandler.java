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

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.common.PluggableExceptionHandler;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.extensibility.ExceptionListener;
import pt.org.aguiaj.extensibility.ExceptionTrace;
import pt.org.aguiaj.standard.StandardNamePolicy;

public enum ExceptionHandler {
	INSTANCE;
	
	private List<SpecificExceptionHandler> handlers;
	private Set<Member> previousMethodErrors;
	private List<ExceptionListener> listeners;
	
	private ExceptionTrace trace;
	
	private String[] lastArgs;
	
	private ExceptionHandler() {
		handlers = new ArrayList<SpecificExceptionHandler>();
		previousMethodErrors = new HashSet<Member>();
		listeners = new ArrayList<ExceptionListener>();
	}

	public void addHandler(SpecificExceptionHandler handler) {
		handlers.add(handler);		
	}
	
	public String[] getLastArgs() {
		return lastArgs;
	}
	
	public void clearErrors() {
		previousMethodErrors.clear();
	}

	public void addListener(ExceptionListener l) {
		listeners.add(l);
	}
	
	public synchronized void handleException(Member member, String[] args, Throwable exception) {
		String message = exception.getMessage();
		if(message == null)
			message = "";
		
		String title = StandardNamePolicy.prettyClassName(exception.getClass());
		int icon = MessageDialog.ERROR;

		if(exception instanceof IllegalArgumentException || 
				exception instanceof IllegalStateException ||
				exception instanceof NullPointerException && exception.getMessage() != null) {
			icon = MessageDialog.WARNING;	
		}
		else if(exception instanceof StackOverflowError) {
			title = UIText.STACK_OVERFLOW.get();
			String methodText = member.getDeclaringClass().getSimpleName() + "." + member.getName() + "(..)";
			message = UIText.CHECK_METHOD_RECURSION.get(methodText);
			if(member != null)
				previousMethodErrors.add(member);
		}
		else if(exception instanceof OutOfMemoryError) {
			title = UIText.OUT_OF_MEMORY.get();
			String methodText = member.getDeclaringClass().getSimpleName() + "." + member.getName() + "(..)";
			message = "Check method " + methodText + ".";
			if(member != null)
				previousMethodErrors.add(member);
		}
		else if(exception instanceof Error) {
			title = UIText.COMPILATION_ERRORS.get();
			int line = exception.getStackTrace()[0].getLineNumber();
			String className = exception.getStackTrace()[0].getClassName();
			message = UIText.CHECK_CLASS_AT.get(className, line, exception.getMessage());
			if(member != null)
				previousMethodErrors.add(member);
		}
		else {
			for(SpecificExceptionHandler handler : handlers)
				if(handler.getClass().getAnnotation(PluggableExceptionHandler.class).value().equals(exception.getClass()))
					message = handler.getMessage(exception);
		}
		
		trace = new ExceptionTrace(exception, message, args);
		
//		SWTUtils.showMessage(title, message, icon);
		
		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), title, null,
			    message, icon, new String[] {"Go to error", "OK"}, 1);
		
		if(dialog.open() == 0) {
//		if(icon == SWT.ERROR)
			for(ExceptionListener l : listeners)
				l.newException(trace);
		}
//		int result = dialog.open();
	}
	
}
