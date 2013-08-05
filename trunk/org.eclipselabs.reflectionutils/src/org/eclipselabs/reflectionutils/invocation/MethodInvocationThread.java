package org.eclipselabs.reflectionutils.invocation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodInvocationThread extends InvocationThread {
	private Method method;
	private Object object;
	private Object[] args;

	public MethodInvocationThread(Method method, Object object, Object[] args) {
		this.method = method;
		this.object = object;
		this.args = args;
	}

	@Override
	protected Object execute() throws InvocationTargetException {
		method.setAccessible(true);
		try {
			return method.invoke(object, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
}
