package pt.org.aguiaj.objects;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.JavaCommandWithReturn;
import pt.org.aguiaj.standard.StandardNamePolicy;

import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Lists.*;
// singleton
// observable
public class ObjectModel {

	public interface EventListener {
		void newObjectEvent(Object obj);
		void removeObjectEvent(Object obj);
		void newReferenceEvent(Reference ref);
		void changeReferenceEvent(Reference ref);
		void removeReferenceEvent(Reference ref);
		void commandExecuted(JavaCommand cmd);
		void commandRemoved(JavaCommand cmd);
		void clearAll();
	}

	public static abstract class EventListenerAdapter implements EventListener {
		public void newObjectEvent(Object obj) { }
		public void removeObjectEvent(Object obj) { }
		public void newReferenceEvent(Reference ref) { }
		public void changeReferenceEvent(Reference ref) { }
		public void removeReferenceEvent(Reference ref) { }
		public void commandExecuted(JavaCommand cmd) { }
		public void commandRemoved(JavaCommand cmd) { }
		public void clearAll() { }
	}

	private static ObjectModel instance;

	private static class NullObject {  }
	private static final NullObject NULL_OBJECT = new NullObject();

	private Map<String, Object> referenceTable;
	private Map<String, Class<?>> referenceTypeTable;
	private IdentityObjectSet objectSet;

	private LinkedList<JavaCommand> activeCommands;

	private Set<EventListener> listeners;

	// LISTEN TO CLASS MODEL


	private ObjectModel() {
		referenceTable = newLinkedHashMap();
		referenceTypeTable = newHashMap();
		objectSet = new IdentityObjectSet();
		activeCommands = newLinkedList();
		listeners = newHashSet();
	}

	public static ObjectModel getInstance() {
		if(instance == null)
			instance = new ObjectModel();

		return instance;
	}	


	public void addEventListener(EventListener listener) {
		listeners.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		listeners.remove(listener);
	}

	private EventListener[] listeners() {
		return listeners.toArray(new EventListener[listeners.size()]);
	}

	public void addObject(Object object) {
		if(!objectSet.contains(object)) {
			objectSet.add(object);
			for(EventListener l : listeners())
				l.newObjectEvent(object);
		}
	}

	public void removeObject(Object object) {
		assert objectSet.contains(object);

		objectSet.remove(object);

		Iterator<Entry<String, Object>> it = referenceTable.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if(entry.getValue() == object) {
				it.remove();
				referenceTypeTable.remove(entry.getKey());
			}
		}

		for(EventListener l : listeners())
			l.removeObjectEvent(object);
	}


	public void removeDeadObjects() {
		for(Object o : objectSet.objects()) {
			if(isDeadObject(o)) {
				objectSet.remove(o);
				for(EventListener l : listeners())
					l.removeObjectEvent(o);
			}
		}
	}

	public void addStaticReferences(Collection<Class<?>> classes) {

		for(Class<?> type : classes) {
			List<Field> fields = ClassModel.getInspector().getVisibleStaticAttributes(type);
			fields.addAll(ClassModel.getInspector().getEnumFields(type));

			for(Field f : fields)  {
				Object obj = null;
				try {
					obj = f.get(null);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				addReference(f.getType(), obj, Reference.staticReference(type, f), false);
			}
		}
	}

	public void changeReference(String name, Object obj) {
		referenceTable.put(name, obj);
		addObject(obj);
		for(EventListener l : listeners())
			l.changeReferenceEvent(new Reference(name, referenceTypeTable.get(name), obj));
	}

	public void removeReference(String name) {
		assert referenceTable.containsKey(name);

		Reference ref = new Reference(
				name, 
				referenceTypeTable.get(name),
				null);

		referenceTable.remove(name);
		referenceTypeTable.remove(name);

		for(EventListener l : listeners())
			l.removeReferenceEvent(ref);

		for(Iterator<JavaCommand> it = activeCommands.iterator(); it.hasNext(); ) {
			JavaCommand cmd = it.next();
			if(cmd.getReference().equals(name)) {
				it.remove();
				for(EventListener l : listeners())
					l.commandRemoved(cmd);
			}
		}
	}


	public void clearAll() {
		referenceTable.clear();
		referenceTypeTable.clear();
		objectSet.clear();
		activeCommands.clear();
		System.gc();
		for(EventListener l : listeners())
			l.clearAll();
	}





	public void execute(JavaCommand command) {
		assert command != null;

		command.execute();

		if(!command.failed())
			addToStack(command);

		if(!command.failed()) {
			if(command instanceof JavaCommandWithReturn) {
				JavaCommandWithReturn cmd = (JavaCommandWithReturn) command;

				Class<?> retType = cmd.getReferenceType();
				if(!retType.isPrimitive() && !retType.equals(void.class)) {
					addReference(
							retType, 
							cmd.getResultingObject(), 
							cmd.getReference(),
							true);
				}
			}	
		}
	}


	private void addReference(Class<?> type, Object object, String name, boolean notify) {
		referenceTable.put(name, object == null? NULL_OBJECT : object);
		referenceTypeTable.put(name, type);

		if(object != null)
			objectSet.add(object);

		if(notify) {
			for(EventListener l : listeners())
				l.newReferenceEvent(new Reference(name, type, object));
		}
	}





	// functions------------------------------------------------------

	public Object[] getAllObjects() {
		return getObjects(Object.class);
	}

	public Object[] getObjects(Class<?> type) {
		assert type != null;

		IdentityObjectSet set = new IdentityObjectSet();

		for(Object o : objectSet.objects())
			if(!(o == NULL_OBJECT) && type.isInstance(o) && !isDeadObject(o))
				set.add(o);

		return set.objects();
	}

	public boolean objectExists(Object object) {
		return objectSet.contains(object);
	}

	public static Object getObject(String reference) {
		Object obj = instance.referenceTable.get(reference);

		return obj == NULL_OBJECT ? null : obj;
	}






	public Map<String, Reference> getReferenceTable() {
		Map<String, Reference> table = newHashMap();
		for(String key : referenceTable.keySet()) {
			Object obj = referenceTable.get(key);
			if(obj == NULL_OBJECT)
				obj = null;
			table.put(key, new Reference(key, referenceTypeTable.get(key), obj));
		}
		return table;
	}

	public List<Reference> getCompatibleReferences(Class<?> type) {
		List<Reference> refs = new ArrayList<Reference>();

		for(String r : referenceTable.keySet()) {
			Class<?> refType = referenceTypeTable.get(r);

			if(type.isAssignableFrom(refType)) {
				Object obj = referenceTable.get(r);
				if(obj == NULL_OBJECT)
					obj = null;
				refs.add(new Reference(r, refType, obj));
			}
		}

		for(Class<?> c : ClassModel.getInstance().getActivePluginTypes()) {	
			for(Field f : c.getFields()) {
				if(Modifier.isStatic(f.getModifiers()) && 
						type.isAssignableFrom(f.getType())) {
					Object obj = null;
					try {
						obj = f.get(null);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
					refs.add(new Reference(c.getSimpleName() + "." + f.getName(), f.getType(), obj));
				}
			}
		}

		return refs;
	}

	public List<Reference> getReferences(Object object) {
		List<Reference> refs = new ArrayList<Reference>();
		for(String r : referenceTable.keySet()) {
			Object obj = referenceTable.get(r);

			if(obj == object) {
				Class<?> refType = referenceTypeTable.get(r);
				refs.add(new Reference(r, refType, object));
			}
		}
		return refs;
	}

	public boolean isReferenceInUse(String reference) {
		assert reference != null;
		return referenceTable.containsKey(reference);
	}

	public boolean isNullReference(String reference) {		
		return 
				referenceTable.containsKey(reference) && 
				referenceTable.get(reference) == NULL_OBJECT;
	}


	public static Class<?> getReferenceType(String reference) {
		return instance.referenceTypeTable.get(reference);
	}


	public static Reference getFirstReference(Object object) {
		return instance.getFirstReferenceAux(object);
	}

	private Reference getFirstReferenceAux(Object object) {
		for(String ref : referenceTable.keySet()) {
			if(referenceTable.get(ref) == object)
				return new Reference(ref, referenceTypeTable.get(ref), object);
		}		
		return null;
	}


	public List<Reference> getNullReferences() {
		List<Reference> nullRefs = new ArrayList<Reference>();
		for(String ref : referenceTable.keySet()) {
			Object obj = referenceTable.get(ref);
			if(obj == NULL_OBJECT) {
				Class<?> refType = referenceTypeTable.get(ref);
				nullRefs.add(new Reference(ref, refType , null));
			}
		}
		return nullRefs;
	}	

	public Object[] getDeadObjects() {
		IdentityObjectSet dead = new IdentityObjectSet();

		for(Object object : objectSet.objects())
			if(isDeadObject(object))
				dead.add(object);

		return dead.objects();
	}

	private boolean isDeadObject(Object object) {
		return objectSet.contains(object) && !referenceTableContainsObject(object);
	}

	private boolean referenceTableContainsObject(Object object) {
		for(Object o : referenceTable.values())
			if(o == object)
				return true;
		return false;
	}


	public String nextReference(Class<?> clazz) {			
		String refLetter = StandardNamePolicy.baseReferenceName(clazz);
		int i = 1;
		String ref = refLetter + i;
		while(referenceTable.containsKey(ref)) {
			i++;
			ref = refLetter + i;
		}
		return ref;
	}




	// ---------------------- COMMANDS

	private void addToStack(JavaCommand command) {
		activeCommands.add(command);
		for(EventListener l : listeners)
			l.commandExecuted(command);
	}

	public List<JavaCommand> getActiveCommands() {
		return activeCommands;
	}

	public boolean isFirstCommand(JavaCommand command) {
		return activeCommands.getFirst() == command;
	}

	public boolean isLastCommand(JavaCommand command) {
		return activeCommands.getLast() == command;
	}

	public JavaCommand getLastCommand() {
		return activeCommands.isEmpty() ? null : activeCommands.getLast();
	}

	public JavaCommand getCommandBefore(JavaCommand command) {
		if(command == null && !activeCommands.isEmpty())
			return activeCommands.getLast();

		int index = activeCommands.indexOf(command);
		if(index - 1 >= 0)
			return activeCommands.get(index - 1);

		return null;
	}

	public JavaCommand getCommandAfter(JavaCommand command) {
		int index = activeCommands.indexOf(command);
		if(index + 1 < activeCommands.size())
			return activeCommands.get(index + 1);

		return null;
	}


}
