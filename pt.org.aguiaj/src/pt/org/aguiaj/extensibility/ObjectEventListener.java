package pt.org.aguiaj.extensibility;
public interface ObjectEventListener {
	void newObjectEvent(Object obj);
	void removeObjectEvent(Object obj);
	void newReferenceEvent(Reference ref);
	void changeReferenceEvent(Reference ref);
	void removeReferenceEvent(Reference ref);
	void commandExecuted(JavaCommand cmd);
	void commandRemoved(JavaCommand cmd);
	void clearAll();
}