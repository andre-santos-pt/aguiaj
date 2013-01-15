package pt.org.aguiaj.extensibility;

public interface ExceptionListener {

	void newException(ExceptionTrace trace, boolean goToError);
}
