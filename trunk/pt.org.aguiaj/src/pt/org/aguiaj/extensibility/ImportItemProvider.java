package pt.org.aguiaj.extensibility;


public interface ImportItemProvider  {
	
	Class<?> getType();
	String getInstruction(String filePath);
}
