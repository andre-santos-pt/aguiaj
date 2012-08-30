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
package pt.org.aguiaj.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvocationThread extends Thread {
		private Method method;
		private Object object;
		private Object[] args;
		
		private Object resultingObject;
		private boolean failed;
		private Exception exception;
		
		public MethodInvocationThread(Method method, Object object, Object[] args) {
			this.method = method;
			this.object = object;
			this.args = args;
			setPriority(MIN_PRIORITY);
		}
		
		public Object getResultingObject() {
			return resultingObject;
		}
		
		public boolean hasFailed() {
			return failed;
		}

		public Exception getException() {
			return exception;
		}		
		
		public void run() {
			executeMethod();
		}
		
		public void executeMethod() {
			try {
				method.setAccessible(true);
				resultingObject = method.invoke(object, args);
			} 			
			catch(InvocationTargetException userCodeException) {
				exception = userCodeException;
				failed = true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
