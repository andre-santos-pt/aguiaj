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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.SWT;

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.CommandsCommon;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;


public class ConstructorInvocationCommand extends JavaCommandWithReturn implements SeparateThreadCommand {

	private Constructor<?> constructor;
	private Object[] args;
	private Object resultingObject;

	private String reference;
	private Class<?> referenceType;

	private ConstructorInvocationThread thread; 

	public ConstructorInvocationCommand(final Constructor<?> constructor, Object[] args) {
		this(constructor, args, ObjectModel.aspectOf().nextReference(constructor.getDeclaringClass()), constructor.getDeclaringClass());
	}
	
	public ConstructorInvocationCommand(final Constructor<?> constructor, Object[] args, String reference, Class<?> referenceType) {
		assert constructor != null;
		assert args != null;
		assert reference != null;
		assert ReflectionUtils.checkParamTypes(constructor.getParameterTypes(), args);

		this.constructor = constructor;
		this.args = args;
		this.reference = reference;
		this.referenceType = referenceType;				
	}




	public String getJavaInstruction() {			
		if(reference != null && referenceType != null) {
			String refType = referenceType.getSimpleName();	
			return refType + " " + reference + " = new " + invocationInstruction();
		}
		else {
			return "new " + invocationInstruction();
		}
	}

	private String invocationInstruction() {
		return constructor.getDeclaringClass().getSimpleName() + "(" + CommandsCommon.buildParams(args) + ")";
	}


	public synchronized void waitToFinish() {
		try {
			thread.join(AguiaJParam.METHOD_TIMEOUT.getInt() * 1000);
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean failed() {
		if(thread.isAlive())
			return true;
		else
			return thread.hasFailed();
	}

	@SuppressWarnings("deprecation")
	public void execute() {
		constructor.setAccessible(true);
		thread = new ConstructorInvocationThread(constructor, args);

		thread.start();

		waitToFinish();
		if(thread.isAlive()) {
			thread.interrupt();
			thread.stop();
			String message = UIText.INFINITE_CYCLE_AT.get(invocationInstruction());
			SWTUtils.showMessage(UIText.TOO_LONG_TIME.get(), message, SWT.ICON_WARNING);
		}
		else if(thread.exception != null) {
			ExceptionHandler.INSTANCE.handleException(null, null, thread.exception.getCause());
		}				
	}


	public String getReference() {
		return reference;
	}


	public Object getResultingObject() {
		return resultingObject;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	@Override
	public Class<?> getReferenceType() {		
		return referenceType;
	}

	
	
	private class ConstructorInvocationThread extends Thread {
		private Constructor<?> constructor;		
		private Object[] args;
		private boolean failed;

		Exception exception;

		ConstructorInvocationThread(Constructor<?> constructor, Object[] args) {
			this.constructor = constructor;			
			this.args = args;
		}

		public boolean hasFailed() {
			return failed;
		}

		public void run() {
			execute();
		}
		
		public void execute() {
			try {
				resultingObject = constructor.newInstance(args);
			} 
			catch(InvocationTargetException userCodeException) {
				exception = userCodeException;
				failed = true;
			}
			catch (Exception e) {
				failed = true;
			}
		}
	}

}
