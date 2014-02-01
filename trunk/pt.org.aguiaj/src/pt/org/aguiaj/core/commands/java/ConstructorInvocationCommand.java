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
package pt.org.aguiaj.core.commands.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;

import pt.org.aguiaj.common.ConstructorInvocationThread;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.objects.ObjectModel;

public class ConstructorInvocationCommand extends JavaCommandWithArgs implements ContractAware {

	private Constructor<?> constructor;

	private String reference;
	private Class<?> referenceType;

	private ConstructorInvocationThread thread;
	
	private RuntimeException invariantException;
	
	public ConstructorInvocationCommand(final Constructor<?> constructor, Object[] args, String[] argsText) {
		this(constructor, args, argsText, ObjectModel.getInstance().nextReference(constructor.getDeclaringClass()), constructor.getDeclaringClass());
	}
	
	public ConstructorInvocationCommand(final Constructor<?> constructor, Object[] args, String[] argsText, String reference, Class<?> referenceType) {
		super(args, argsText);
		assert constructor != null;
		assert reference != null;
		assert ReflectionUtils.checkParamTypes(constructor.getParameterTypes(), args);

		this.constructor = constructor;
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
		return constructor.getDeclaringClass().getSimpleName() + params();
	}


	public void execute() {	
		thread.executeConstructor();
		if(thread.getResultingObject() != null)
			invariantException = ObjectModel.getInstance().verifyInvariantOnCreation(thread.getResultingObject());
	}


	public String getReference() {
		return reference;
	}


	public Object getResultingObject() {
		return thread.getResultingObject();
	}

	public RuntimeException getException() {
		if(!failed())
			return null;
		
		if(invariantException != null)
			return invariantException;
		
		Throwable t = thread.getException().getCause();
		return t != null && t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException("Problems in constructor");
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
		return thread.hasFailed() || invariantException != null;
	}

	@Override
	public Object getObjectUnderContract() {
		return thread.getResultingObject();
	}

	@Override
	public Member getMember() {
		return constructor;
	}

	

}
