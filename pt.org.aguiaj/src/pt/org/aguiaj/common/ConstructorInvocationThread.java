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
package pt.org.aguiaj.common;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.UIText;

public class ConstructorInvocationThread {
	private Constructor<?> constructor;
	private Object[] args;
	private String instruction;
	
	private Object resultingObject;
	private Exception exception;

	public ConstructorInvocationThread(Constructor<?> constructor, Object[] args, String instruction) {
		this.constructor = constructor;
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

	public void executeConstructor() {
		Thread thread = new Thread() {
			public void run() {
				try {
					constructor.setAccessible(true);
					resultingObject = constructor.newInstance(args);
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
