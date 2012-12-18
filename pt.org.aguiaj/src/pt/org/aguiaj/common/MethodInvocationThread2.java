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
package pt.org.aguiaj.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.UIText;

public class MethodInvocationThread2 {
	private Method method;
	private Object object;
	private Object[] args;
	private String instruction;
	
	private Object resultingObject;
	private Exception exception;

	public MethodInvocationThread2(Method method, Object object, Object[] args, String instruction) {
		this.method = method;
		this.object = object;
		this.args = args;
		this.instruction = instruction;
	}

	public Object getResultingObject() {
		return resultingObject;
	}

	public boolean hasFailed() {
		return exception != null;
	}

	public Exception getException() {
		return exception;
	}		

	public void executeMethod() {
		Thread thread = new Thread() {
			public void run() {
				try {
					method.setAccessible(true);
					resultingObject = method.invoke(object, args);
				} 			
				catch(InvocationTargetException userCodeException) {
					exception = userCodeException;
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();

		try {
			thread.join(AguiaJParam.METHOD_TIMEOUT.getInt() * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(thread.isAlive()) {					
			thread.interrupt();					
			thread.stop();
			exception = new RuntimeException(UIText.INFINITE_CYCLE_AT.get(instruction));
		}
	}
}
