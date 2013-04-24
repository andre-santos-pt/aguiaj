package aguiaj.console;

import java.util.ArrayList;
import java.util.List;

//import pt.org.aguiaj.console.ConsoleInterface;

public class Console { //implements ConsoleInterface {

	private static Console instance;
	
	private List<String> lines;
	
	private Console() {
		lines = new ArrayList<String>();
		endLine();
	}
	
	public static Console getInstance() {
		if(instance == null)
			instance = new Console();
		
		return instance;
	}
	
	public void printObject(Object object) {
		lines.set(lines.size()-1, lines.get(lines.size()-1) + toString(object));
	}
	
	public static void print(Object object) {
		getInstance().printObject(object);
	}
	
	public void endLine() {
		lines.add("");
	}
	
	public static void newline() {
		getInstance().endLine();
	}
	
	public static void println(Object object) {
		print(object);
		newline();
	}
	
	public void clearLines() {
		lines.clear();
		endLine();
	}
	
	public static void clear() {
		getInstance().clearLines();
	}
	
	public int getNumberOfLines() {
		return lines.size();
	}
	
	public String getLine(int index) {
		if(index < 0  || index >= lines.size())
			throw new IllegalArgumentException("Invalid line index: " + index);
		
		return lines.get(index);
	}
	
	private String toString(Object object) {
		return object == null ? "null" : object.toString();
	}
}
