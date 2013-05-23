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

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import pt.org.aguiaj.common.MethodInvocationThread2;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.objects.ObjectModel;


public class MethodInvocationCommand extends JavaCommandWithArgs implements ContractAware {	
	private Object object;
	private String objectReference;
	private Method method;
	private String reference;
	private final MethodInvocationThread2 thread;

	private static final Object[] ZERO_LENGTH_OBJECT_ARRAY = new Object[0];
	private static final String[] ZERO_LENGTH_STRING_ARRAY = new String[0];
	
	public MethodInvocationCommand(Object object, Method method) {
		this(object, null, method);
	}
	
	public MethodInvocationCommand(Object object, String objectReference, Method method) {
		this(object, objectReference, method, ZERO_LENGTH_OBJECT_ARRAY, ZERO_LENGTH_STRING_ARRAY);
	}

	
	public MethodInvocationCommand(Object object, String objectReference, Method method, Object[] args, String[] argsText) {
		super(args, argsText);
		
		assert method != null;

		assert 
		Modifier.isStatic(method.getModifiers()) && object == null || 
		!Modifier.isStatic(method.getModifiers()) && object != null;

		assert ReflectionUtils.checkParamTypes(method.getParameterTypes(), args);

		this.object = object;
		this.method = method;
		this.reference = ObjectModel.getInstance().nextReference(method.getReturnType()); // TODO: rever ref compativel
		this.objectReference = objectReference;

		thread = new MethodInvocationThread2(this.method, object, args, invocationInstruction());
	}

	public static MethodInvocationCommand instanceInvocation(Object object, String methodName) {
		if(object == null)
			throw new NullPointerException("object cannot be null");
		
		Method method;
		try {
			method = object.getClass().getDeclaredMethod(methodName);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
		return new MethodInvocationCommand(object, null, method);
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

	
	public void execute() throws RuntimeException {
		
		thread.executeMethod();
		if(thread.getException() != null) {
			Throwable t = thread.getException().getCause();
			throw new RuntimeException(t != null ? t : thread.getException());
//			throw t != null ? t : thread.getException();
//			ExceptionHandler.INSTANCE.handleException(method, argsText, t != null ? t : thread.getException());
		}
	}

	public String getReference() {
		return reference;
	}


	public Object getResultingObject() {
		return thread.getResultingObject();
	}


	@Override
	public Class<?> getReferenceType() { 
		return method.getReturnType();
	}

	@Override
	public boolean failed() {
		return thread.hasFailed();
	}

	@Override
	public Object getObjectUnderContract() {
		return object;
	}

	@Override
	public Member getMember() {
		return method;
	}
}
