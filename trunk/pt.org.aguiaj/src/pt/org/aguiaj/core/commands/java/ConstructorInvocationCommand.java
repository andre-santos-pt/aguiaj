/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.commands.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.SWT;

import pt.org.aguiaj.common.ConstructorInvocationThread;
import pt.org.aguiaj.common.InfiniteCycleException;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.CommandsCommon;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.objects.ObjectModel;

public class ConstructorInvocationCommand extends JavaCommandWithReturn {

	private Constructor<?> constructor;
	private Object[] args;

	private String reference;
	private Class<?> referenceType;

	private ConstructorInvocationThread thread;
	
	public ConstructorInvocationCommand(final Constructor<?> constructor, Object[] args) {
		this(constructor, args, ObjectModel.getInstance().nextReference(constructor.getDeclaringClass()), constructor.getDeclaringClass());
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
		
		thread = new ConstructorInvocationThread(constructor, args, invocationInstruction());
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


	public void execute() {	
		thread.executeConstructor();
		if(thread.getException() != null) {
			Throwable t = thread.getException().getCause();
			ExceptionHandler.INSTANCE.handleException(constructor, null, t != null ? t : thread.getException());
		}		
	}


	public String getReference() {
		return reference;
	}


	public Object getResultingObject() {
		return thread.getResultingObject();
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	@Override
	public Class<?> getReferenceType() {		
		return referenceType;
	}

	@Override
	public boolean failed() {
		return thread.hasFailed();
	}
}
