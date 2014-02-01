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
package org.eclipselabs.javainterpreter;


public class Test {


	public static void main(String[] args) {
		SimpleContext context = new SimpleContext(Test.class);
		JavaInterpreter jint = new JavaInterpreter(context);
		Object o = jint.evaluateMethodInvocation("new Test().toString()");
		System.out.println("? " + o);
	}
}
