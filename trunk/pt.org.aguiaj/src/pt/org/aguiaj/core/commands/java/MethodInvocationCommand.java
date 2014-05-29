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

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import pt.org.aguiaj.common.MethodInvocationThread2;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.ContractUtil;
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
		this.reference = ObjectModel.getInstance().nextReference(getReturnType());
		this.objectReference = objectReference;

		thread = new MethodInvocationThread2(this.method, object, args, invocationInstruction());
	}

	public static MethodInvocationCommand instanceInvocation(Object object, String methodName) {
		return instanceInvocation(object, methodName, new Class[0], new Object[0]);
	}
	
	public static MethodInvocationCommand instanceInvocation(Object object, String methodName, Class<?>[] argTypes, Object[] args) {
		if(object == null)
			throw new NullPointerException("object cannot be null");
		
		Method method;
		try {
			method = object.getClass().getDeclaredMethod(methodName, argTypes);
		} catch (Exception e) {
			try {
				method = object.getClass().getMethod(methodName, argTypes);
			}
			catch(Exception e2) {
				return null;
			}
		}
		
		return new MethodInvocationCommand(object, null, method, args, ZERO_LENGTH_STRING_ARRAY);
	}

	public Method getMethod() {
		return method;
	}
	
	private Class<?> getReturnType() {
		if(object instanceof ContractDecorator) {
			//Object obj = ((ContractDecorator<?>) object).getWrappedObject();
			Object obj = ContractUtil.unwrap(object);
			Class<?> cla = obj.getClass();
			try {
				return cla.getMethod(method.getName(), method.getParameterTypes()).getReturnType();
			} catch (Exception e) {
				return method.getReturnType();
			}
		}
		return method.getReturnType();
	}

	public String getJavaInstruction() {
		Class<?> returnType = getReturnType();

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

	
	public void execute() {
		
		thread.executeMethod();
//		 if(thread.getException() != null) {
//			Throwable t = thread.getException().getCause();
//			throw t != null && t instanceof RuntimeException ? (RuntimeException) t : (RuntimeException) thread.getException();
			
//			throw t != null ? t : thread.getException();
//			ExceptionHandler.INSTANCE.handleException(method, argsText, t != null ? t : thread.getException());
//		}
	}

	public String getReference() {
		return reference;
	}

	public Object getTarget() {
		return object;
	}

	public Object getResultingObject() {
		return thread.getResultingObject();
	}


	@Override
	public Class<?> getReferenceType() { 
		return getReturnType();
	}

	@Override
	public boolean failed() {
		return thread.hasFailed();
	}

	
	public RuntimeException getException() {
		if(!failed())
			return null;
		
		Throwable t = thread.getException().getCause();
		return t != null && t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(thread.getException());
	}
	
	@Override
	public Object getObjectUnderContract() {
//		Object o = object;
//		while(o instanceof ContractDecorator)
//			o = ((ContractDecorator<?>) o).getWrappedObject();
		
		return ContractUtil.unwrap(object);
	}

	@Override
	public Member getMember() {
		return method;
	}
}
