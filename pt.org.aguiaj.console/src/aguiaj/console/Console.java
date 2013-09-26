package aguiaj.console;

import pt.org.aguiaj.console.AguiaJConsoleActivator;


public class Console {
	
	public static void line() {
		line(80);
	}
	
	public static void line(int length) {
		print(buildString(length, '_'));
	}

	private static String buildString(int length, char c) {
		char[] line = new char[length];
		for(int i = 0; i < length; i++)
			line[i] = c;
		return new String(line);
	}
	
	public static void newline() {
		print("");
	}
	
	public static void clear() {
		AguiaJConsoleActivator.getInstance().clearConsole();
	}
	
	public static void print(int tabs, Object obj) {
		print(buildString(tabs, '\t') + obj);
	}
	
	public static void print(Object obj) {
		AguiaJConsoleActivator.getInstance().writeToConsole(obj);
	}
}
