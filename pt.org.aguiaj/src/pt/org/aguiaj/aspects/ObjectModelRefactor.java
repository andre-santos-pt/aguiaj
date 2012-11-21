package pt.org.aguiaj.aspects;

import java.util.Map;

import pt.org.aguiaj.common.Reference;

public class ObjectModelRefactor {

	public interface ObjectCreationEvent {
		void objectAdded(Object obj);
	}
	
	public interface ObjectRemovalEvent {
		void objectRemoved(Object obj);
	}
	
	public interface NewReferenceEvent {
		void referenceAdded(Reference ref);
	}
	
	public interface ReferenceRemovalEvent {
		void referenceRemoved(Reference ref);
	}

	
	private Map<Reference, Object> table;
}
