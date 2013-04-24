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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import pt.org.aguiaj.common.MethodInvocationThread2;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.objects.ObjectModel;


public class MethodInvocationCommand extends JavaCommandWithReturn implements ContractAware {	
	private Object object;
	private String objectReference;
	private Method method;
	private Object[] args;
	private String[] argsText;
	private String reference;
	private final MethodInvocationThread2 thread;


	public MethodInvocationCommand(Object object, String objectReference, Method method, Object[] args, String[] argsText) {
		assert method != null;
		assert args != null;
		assert argsText != null;

		assert 
		Modifier.isStatic(method.getModifiers()) && object == null || 
		!Modifier.isStatic(method.getModifiers()) && object != null;

		assert ReflectionUtils.checkParamTypes(method.getParameterTypes(), args);

		this.object = object;
		this.method = method;
		this.args = args;
		this.argsText = argsText;
		this.reference = ObjectModel.getInstance().nextReference(method.getReturnType()); // TODO: rever ref compativel
		this.objectReference = objectReference;

		thread = new MethodInvocationThread2(this.method, object, args, invocationInstruction());
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

	private String params() {
		String ret = "(";
		for(int i = 0; i < argsText.length; i++) {
			if(i != 0)
				ret += ", ";
			ret += argsText[i];
		}
		return ret + ")";
	}

	public void execute() {
//		Class<?>[] paramTypes = method.getParameterTypes();
//		for(int i = 0; i < args.length; i++)
//			if(paramTypes[i].equals(String.class) && args[i] == null && !argsText.equals("null"))
//				SWTUtils.showMessage("Not a string", "In order to insert a string, write text between \" \"", SWT.ICON_ERROR);

				
		thread.executeMethod();
		if(thread.getException() != null) {
			Throwable t = thread.getException().getCause();
			ExceptionHandler.INSTANCE.handleException(method, argsText, t != null ? t : thread.getException());
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


	public Object[] getArgs() {
		return args;
	}
}
