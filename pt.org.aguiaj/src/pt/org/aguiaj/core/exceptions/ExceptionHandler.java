/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.core.exceptions;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.common.IdentityObjectSet;
import pt.org.aguiaj.common.PluggableExceptionHandler;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.java.JavaCommandWithArgs;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.extensibility.ExceptionListener;
import pt.org.aguiaj.extensibility.ExceptionTrace;
import pt.org.aguiaj.extensibility.TraceLocation;
import pt.org.aguiaj.extensibility.contracts.ContractException;
import pt.org.aguiaj.standard.StandardNamePolicy;

public enum ExceptionHandler {
	INSTANCE;

	private List<SpecificExceptionHandler> handlers;
	private Set<Member> previousMethodErrors;
	private IdentityObjectSet objectsWithProblems;
	private List<ExceptionListener> listeners;
	

	private ExceptionHandler() {
		handlers = new ArrayList<SpecificExceptionHandler>();
		previousMethodErrors = new HashSet<Member>();
		listeners = new ArrayList<ExceptionListener>();
		objectsWithProblems = new IdentityObjectSet();
	}

	public void addHandler(SpecificExceptionHandler handler) {
		handlers.add(handler);		
	}


	public void clearErrors() {
		previousMethodErrors.clear();
		objectsWithProblems.clear();
	}

	public void addListener(ExceptionListener l) {
		listeners.add(l);
	}

	public boolean execute(JavaCommandWithArgs cmd, Object targetObject) {
		cmd.execute();
		
		if(cmd.failed()) {
			handleException(targetObject, cmd.getMember(), cmd.getArgsText(), cmd.getException());
			return false;
		}
		return true;
	}
	
	public boolean execute(JavaCommandWithArgs cmd) {
		if(cmd instanceof MethodInvocationCommand)
			return execute(cmd, ((MethodInvocationCommand) cmd).getTarget());
		else
			return execute(cmd, null);
		
	}
//	public boolean execute(JavaCommandWithArgs cmd) {
//		cmd.execute();
//		
//		if(cmd.failed()) {
//			handleException(null, cmd.getMember(), cmd.getArgsText(), cmd.getException());
//			return false;
//		}
//		return true;
//	}
	
	public void handleException(Object target, Member member, String[] args, Throwable exception) {
		if(exception.getCause() != null)
			exception = exception.getCause();
		
		String message = exception.getMessage();
		if(message == null)
			message = "";
		
		if(target != null) {
			if(objectsWithProblems.contains(target))
				return;
			else
				objectsWithProblems.add(target);
		}

		String title = StandardNamePolicy.prettyClassName(exception.getClass());
		int icon = MessageDialog.ERROR;

		if(exception instanceof IllegalArgumentException || 
				exception instanceof IllegalStateException ||
				exception instanceof NullPointerException && exception.getMessage() != null ||
				exception instanceof ContractException) {
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

		ExceptionTrace exceptionTrace = new ExceptionTrace(exception, message, args);
		List<TraceLocation> trace = exceptionTrace.getTrace();
		boolean goToError = !trace.isEmpty() && trace.get(0).line != 1;

		String[] buttons = goToError ? new String[] {"OK", "Go to error"} : new String[] {"OK"};
		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), title, null,
				message, icon, buttons, 0);

		int result = dialog.open();
		
		if(!trace.isEmpty()) {
			for(ExceptionListener l : listeners)
				l.newException(exceptionTrace, result != 0);
		}			
	}

}
