package org.eclipselabs.javainterpreter;


public class Test {


	public static void main(String[] args) {
		SimpleContext context = new SimpleContext(Test.class);
		JavaInterpreter jint = new JavaInterpreter(context);
		Object o = jint.evaluateMethodInvocation("new Test().toString()");
		System.out.println("? " + o);
	}
}