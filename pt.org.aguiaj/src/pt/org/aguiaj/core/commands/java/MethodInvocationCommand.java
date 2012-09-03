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
package pt.org.aguiaj.core.commands.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.common.MethodInvocationThread;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;


public class MethodInvocationCommand extends JavaCommandWithReturn implements SeparateThreadCommand {	
	private String objectReference;
	private Method method;
	private String[] argsText;
	private String reference;
	private final MethodInvocationThread thread;

	public MethodInvocationCommand(Object object, String objectReference, Method method, Object[] args, String[] argsText) {
		this(object, objectReference, method, args, argsText, ObjectModel.aspectOf().nextReference(method.getReturnType()));
	}

	public MethodInvocationCommand(Object object, String objectReference, Method method, Object[] args, String[] argsText, String reference) {
		assert method != null;
		assert args != null;
		assert argsText != null;

		assert 
		Modifier.isStatic(method.getModifiers()) && object == null || 
		!Modifier.isStatic(method.getModifiers()) && object != null;

		assert ReflectionUtils.checkParamTypes(method.getParameterTypes(), args);

		this.method = method;
		this.argsText = argsText;
		this.reference = reference;
		this.objectReference = objectReference;

		thread = new MethodInvocationThread(method, object, args);
	}



	public Method getMethod() {
		return method;
	}

	public String getJavaInstruction() {
		Class<?> returnType = method.getReturnType();

		String ref = "";
		if(!returnType.isPrimitive())
			ref = returnType.getSimpleName() + " " + reference + " = ";

		String invocation = "." +  invocationInstruction();

		if(Modifier.isStatic(method.getModifiers()))	
			return ref + method.getDeclaringClass().getSimpleName() + invocation;
		else
			return ref + objectReference + invocation;
	}

	private String invocationInstruction() {
		return method.getName() + params();
	}
	
	private String params() {
		String ret = "(";
		for(int i = 0; i < argsText.length; i++) {
			if(i != 0)
				ret += ", ";
			ret += argsText[i];
		}
		return ret + ")";
	}

	public synchronized void waitToFinish() {
		try {
			thread.join(AguiaJParam.METHOD_TIMEOUT.getInt() * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public synchronized boolean failed() {
		if(thread.isAlive())
			return true;
		else
			return thread.hasFailed();
	}

	public synchronized void execute() {
		thread.start();

		Runnable runnable = new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				waitToFinish();
				if(thread.isAlive()) {					
					thread.interrupt();					
					thread.stop();	
					String message = UIText.INFINITE_CYCLE_AT.get(invocationInstruction());
					SWTUtils.showMessage(UIText.TOO_LONG_TIME.get(), message, SWT.ICON_WARNING);
				}		
				else if(thread.getException() != null) {
					Throwable t = thread.getException().getCause();
					ExceptionHandler.INSTANCE.handleException(method, argsText, t != null ? t : thread.getException());
				}
			}			
		};		
		Display.getDefault().asyncExec(runnable);
	}

	public String getReference() {
		return reference;
	}


	public Object getResultingObject() {
		try {
			thread.join(AguiaJParam.METHOD_TIMEOUT.getInt() * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return thread.getResultingObject();
	}


	@Override
	public Class<?> getReferenceType() { 
		return method.getReturnType();
	}
}
