package org.eclipselabs.reflectionutils.invocation;
import java.lang.reflect.InvocationTargetException;


public abstract class InvocationThread {
	private Object resultingObject;
	private Throwable exception;
	private boolean timeout;
	
	public final Object getResultingObject() {
		return resultingObject;
	}

	public final boolean hasFailed() {
		return exception != null;
	}

	public final Throwable getException() {
		return exception;
	}		

	public boolean timeoutReached() {
		return timeout;
	}
	
	public final void execute(int timeout) {
		this.timeout = false;
		Thread thread = new Thread() {
			public void run() {
				try {
					resultingObject = execute();
				} 			
				catch(InvocationTargetException userCodeException) {
					exception = userCodeException.getCause();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		thread.start();

		try {
			thread.join(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if(thread.isAlive()) {					
			thread.interrupt();					
			thread.stop();
			this.timeout = true;
		}
	}

	protected abstract Object execute() throws InvocationTargetException;

}
