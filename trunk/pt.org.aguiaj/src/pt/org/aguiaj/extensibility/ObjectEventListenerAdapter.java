package pt.org.aguiaj.extensibility;
public abstract class ObjectEventListenerAdapter implements ObjectEventListener {
	public void newObjectEvent(Object obj) { }
	public void removeObjectEvent(Object obj) { }
	public void newReferenceEvent(Reference ref) { }
	public void changeReferenceEvent(Reference ref) { }
	public void removeReferenceEvent(Reference ref) { }
	public void commandExecuted(JavaCommand cmd) { }
	public void commandRemoved(JavaCommand cmd) { }
	public void clearAll() { }
}