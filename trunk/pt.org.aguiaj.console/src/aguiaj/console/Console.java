package aguiaj.console;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import pt.org.aguiaj.console.AguiaJConsoleActivator;

/**
 * Static class, representing a console useful for debugging purposes.
 * @author Andre L. Santos
 */
public class Console {

	
	/**
	 * Prints an object in the console.
	 * @param obj object to print
	 */
	public static void print(Object obj) {
		AguiaJConsoleActivator console = getConsole();
		if(console != null)
			console.writeToConsole(obj);
	}
	
	/**
	 * Prints an object in the console, with a number of tabs preceeding it.
	 * @param tabs an integer equal or greater than zero
	 * @param obj object to print
	 */
	public static void print(int tabs, Object obj) {
		if(tabs < 0)
			throw new IllegalArgumentException("number of tabs must equal or greater than zero");
		
		print(buildString(tabs, '\t') + obj);
	}
	
	/**
	 * Prints a line with the length of 80 characters.
	 */
	public static void line() {
		line(80);
	}

	/**
	 * Prints a line with the given length.
	 * @param length a positive integer
	 */
	public static void line(int length) {
		if(length < 1)
			throw new IllegalArgumentException("line length must be positive");
			
		print(buildString(length, '_'));
	}

	/**
	 * Prints a new (empty) line.
	 */
	public static void newline() {
		print("");
	}

	/**
	 * Clears the console.
	 */
	public static void clear() {
		AguiaJConsoleActivator console = getConsole();

		if(console != null)
			console.clearConsole();
	}

	

	private static String buildString(int length, char c) {
		char[] line = new char[length];
		for(int i = 0; i < length; i++)
			line[i] = c;
		return new String(line);
	}
	
	private static AguiaJConsoleActivator getConsole() {
		AguiaJConsoleActivator console = AguiaJConsoleActivator.getInstance();
		if(console == null)
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", "Could not load AGUIA/J console.");
		return console;
	}

}
