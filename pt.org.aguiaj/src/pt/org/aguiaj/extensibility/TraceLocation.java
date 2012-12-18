package pt.org.aguiaj.extensibility;

public class TraceLocation {

	public final String fileName;
	public final int line;

	public TraceLocation(StackTraceElement element) {
		fileName = element.getClassName().replace('.', '/').concat(".java");
		line = element.getLineNumber();
	}
	
	@Override
	public String toString() {
		return fileName + ":" + line;
	}

}
