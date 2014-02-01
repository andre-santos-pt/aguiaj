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
package pt.iscte.dcti.expressionsview;

import org.eclipselabs.javainterpreter.ExecutionException;
import org.eclipselabs.javainterpreter.JavaInterpreter;
import org.eclipselabs.javainterpreter.Output;


public class Expression {

	private JavaInterpreter interpreter;
	private String value;
	private ExecutionException exception;
	
	public Expression(JavaInterpreter interpreter, String value) {
		this.interpreter = interpreter;
		setValue(value);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String v) {
		value = v.trim();
//		while(!value.isEmpty() && value.endsWith(";"))
//			value = value.substring(0, value.length()-1);
	}
	
//	public void setClass(Class<?> clazz) {
//		interpreter.addClass(clazz);
//	}
	
	public boolean valid() {
		exception = null;
		try { 
			interpreter.evaluateMethodInvocation(getValue());
		}
		catch(ExecutionException ex) {
			exception = ex;
			return false;
		}
		catch(RuntimeException ex) {
			return false;
		}
		return true;
	}
	
	public String evaluate() {
		return Output.get(interpreter.evaluateMethodInvocation(value));
	}

	public String getErrorMessage() {
		return !valid() ? "Syntax error" : "";
	}
	
	public ExecutionException getException() {
		return exception;
	}

	
	@Override
	public String toString() {
		return value;
	}

	
	
}
