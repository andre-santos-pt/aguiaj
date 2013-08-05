package org.eclipselabs.reflectionutils.invocation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class ConstructorInvocationThread extends InvocationThread {
	
	private Constructor<?> constructor;
	private Object[] args;
	
	public ConstructorInvocationThread(Constructor<?> constructor, Object[] args) {
		this.constructor = constructor;
		this.args = args;
	}

	@Override
	protected Object execute() throws InvocationTargetException {
		constructor.setAccessible(true);
		try {
			return constructor.newInstance(args);
		} catch(IllegalAccessException e) {
			e.printStackTrace();
		} catch(InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}
}