package pt.org.aguiaj.core.commands.java;

import java.lang.reflect.Member;

public abstract class JavaCommandWithArgs extends JavaCommandWithReturn {

	private Object[] args;
	private String[] argsText;
	
	public JavaCommandWithArgs(Object[] args, String[] argsText) {
		if(args == null)
			throw new NullPointerException("args cannot be null");
		
		this.args = args;
		this.argsText = argsText;
	}

	public Object[] getArgs() {
		return args;
	}
	
	public String[] getArgsText() {
		return argsText;
	}
	
	public String params() {
		if(argsText == null)
			return "(...)";
		
		String ret = "(";
		for(int i = 0; i < argsText.length; i++) {
			if(i != 0)
				ret += ", ";
			ret += argsText[i];
		}
		return ret + ")";
	}
	
	public abstract RuntimeException getException();
	public abstract Member getMember();
	
}
