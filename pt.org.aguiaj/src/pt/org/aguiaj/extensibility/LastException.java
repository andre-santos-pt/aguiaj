package pt.org.aguiaj.extensibility;
public class LastException {
	public final String message;
	public final String fileName;
	public final int line;
	public final String[] args;

	public LastException(StackTraceElement element, String message, String[] args) {
		fileName = element.getFileName();
		line = element.getLineNumber();
		this.message = message;
		this.args = args;
	}	
}