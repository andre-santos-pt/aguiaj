package aguiaj.console;

import pt.org.aguiaj.core.AguiaJActivator;

public class Console {
	
	public static void line() {
		print("-------------------------------------------");
	}
	
	public static void space() {
		print("");
	}
	
	public static void clear() {
		AguiaJActivator.getDefault().clearConsole();
	}
	
	public static void print(int tabs, Object obj) {
		//TODO
	}
	
	public static void print(Object ... objs) {
		AguiaJActivator.getDefault().writeToConsole(objs);
	}
}
