package org.eclipselabs.javainterpreter;

public class ExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final int line;
	
	public ExecutionException(String message, int line) {
		super(message);
		this.line = line;
	}
	
	public int getLine() {
		return line;
	}
}
