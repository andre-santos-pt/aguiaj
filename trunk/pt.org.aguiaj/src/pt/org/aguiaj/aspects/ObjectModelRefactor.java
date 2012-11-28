package pt.org.aguiaj.aspects;

import java.util.Map;
import java.util.Observable;

import pt.org.aguiaj.common.Reference;

// singleton
// observable
public class ObjectModelRefactor {

	public interface ObjectCreationListener {
		void newObjectEvent(Object obj);
	}
	
	
	
	public interface ObjectRemovalListener {
		void removeObjectEvent(Object obj);
	}
	
	public interface NewReferenceListener {
		Class<?> type();
		boolean exactMatch();
		void newReferenceEvent(Reference ref);
	}
	
	public interface ReferenceRemovalListener {
		void removeReferenceEvent(Reference ref);
	}

	
	private Map<Reference, Object> table;
	
	Observable e = new Observable();
	
}
