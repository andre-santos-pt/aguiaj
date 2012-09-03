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

import pt.org.aguiaj.common.PluggableExceptionHandler;

public enum ActiveExceptionHandler {

	ARITHMETIC(ArithmeticExceptionHandler.class),
	ARRAYINDEX(ArrayIndexOutOfBoundsExceptionsHandler.class),
	ARRAYNEGATIVESIZE(NegativeArraySizeExceptionHandler.class),
	NULLPOINTER(NullPointerExceptionHandler.class),
	INDEX(IndexOutOfBoundsExceptionsHandler.class);

	private Class<? extends SpecificExceptionHandler> clazz;

	private ActiveExceptionHandler(Class<? extends SpecificExceptionHandler> clazz) {
		this.clazz = clazz;
	}

	public static void loadExceptionHandlers() {
		for(ActiveExceptionHandler h : values()) {
			if(h.clazz.getInterfaces().length != 1 || !h.clazz.getInterfaces()[0].equals(SpecificExceptionHandler.class))
				System.err.println(PluggableExceptionHandler.class.getSimpleName() + " is used incorrectly in " +
						h.clazz.getSimpleName());
			else {
				try {
					h.clazz.getConstructor();
					ExceptionHandler.INSTANCE.addHandler((SpecificExceptionHandler) h.clazz.newInstance());
				}
				catch(NoSuchMethodException e) {
					System.err.println(h.clazz.getSimpleName() + " should have a parameterless constructor.");
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
