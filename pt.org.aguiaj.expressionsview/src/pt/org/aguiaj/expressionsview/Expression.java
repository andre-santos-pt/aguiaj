package pt.org.aguiaj.expressionsview;

import java.lang.reflect.Array;

import org.eclipselabs.javainterpreter.JavaInterpreter;
import org.eclipselabs.javainterpreter.Output;


public class Expression {

	private JavaInterpreter interpreter;
	private String value;
	
	public Expression(JavaInterpreter interpreter, Class<?> clazz, String value) {
		this.interpreter = interpreter;
		setValue(value);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String v) {
		value = v.trim();
//		while(!value.isEmpty() && value.endsWith(";"))
//			value = value.substring(0, value.length()-1);
	}
	
//	public void setClass(Class<?> clazz) {
//		interpreter.addClass(clazz);
//	}
	
	public boolean validSyntax() {
		try { 
			interpreter.evaluateMethodInvocation(getValue());
		}
		catch(RuntimeException ex) {
			return false;
		}
		return true;
	}
	
	public String evaluate() {
		return Output.get(interpreter.evaluateMethodInvocation(value));
	}

	public String getErrorMessage() {
		return !validSyntax() ? "Syntax error" : "";
	}
	

	
	@Override
	public String toString() {
		return value;
	}

	
	
}
