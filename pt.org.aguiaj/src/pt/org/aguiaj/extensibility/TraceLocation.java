package pt.org.aguiaj.extensibility;

public class TraceLocation {

	public final String fileName;
	public final String className;
	public final String methodName;
	public final int line;

	public TraceLocation(StackTraceElement element) {
		fileName = element.getClassName().replace('.', '/').concat(".java");
		className = element.getClassName();
		methodName = element.getMethodName();
		line = element.getLineNumber();
	}
	
	@Override
	public String toString() {
		return className + "." + methodName + "(...) : " + line;
	}

}
