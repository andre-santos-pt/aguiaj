/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.objects;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.IdentityObjectSet;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.commands.java.ContractAware;
import pt.org.aguiaj.core.commands.java.JavaCommandWithArgs;
import pt.org.aguiaj.core.commands.java.JavaCommandWithReturn;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.ObjectEventListener;
import pt.org.aguiaj.extensibility.Reference;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.standard.StandardNamePolicy;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;


// singleton
// observable
public class ObjectModel {


	private static ObjectModel instance;

	private static class NullObject {  }
	private static final NullObject NULL_OBJECT = new NullObject();

	private Map<String, Object> referenceTable;
	private Map<String, Class<?>> referenceTypeTable;
	private IdentityObjectSet objectSet;

	private Table<Object, Method, ContractDecorator<?>> contracts;

	private LinkedList<JavaCommand> activeCommands;

	private Set<ObjectEventListener> listeners;


	private ObjectModel() {
		referenceTable = newLinkedHashMap();
		referenceTypeTable = newHashMap();
		objectSet = new IdentityObjectSet();
		activeCommands = newLinkedList();
		listeners = newHashSet();

		contracts = HashBasedTable.create();

		for(ObjectEventListener l : listeners)
			l.init();
	}

	public static ObjectModel getInstance() {
		if(instance == null)
			instance = new ObjectModel();

		return instance;
	}	


	public void addEventListener(ObjectEventListener listener) {
		listeners.add(listener);
	}

	public void removeEventListener(ObjectEventListener listener) {
		listeners.remove(listener);
	}

	private ObjectEventListener[] listeners() {
		return listeners.toArray(new ObjectEventListener[listeners.size()]);
	}

	public void addObject(Object object, boolean notify) {
		if(!objectSet.contains(object)) {
			objectSet.add(object);

			if(object != null)
				createContractProxies(object);

			if(notify) {
				for(ObjectEventListener l : listeners())
					l.newObjectEvent(object);
			}
		}
	}

	public void removeObject(Object object) {
		assert objectSet.contains(object);

		objectSet.remove(object);

		for(Iterator<Entry<String, Object>> it = referenceTable.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Object> entry = it.next();
			if(entry.getValue() == object) {
				it.remove();
				Class<?> refType = referenceTypeTable.get(entry.getKey());
				referenceTypeTable.remove(entry.getKey());
				for(ObjectEventListener l : listeners())
					l.removeReferenceEvent(new Reference(entry.getKey(), refType, object));
			}
		}

		for(ObjectEventListener l : listeners())
			l.removeObjectEvent(object);

		for(Iterator<JavaCommand> it = activeCommands.iterator(); it.hasNext(); ) {
			JavaCommand cmd = it.next();
			if( cmd instanceof JavaCommandWithReturn &&
					((JavaCommandWithReturn) cmd).getResultingObject() == object) {
				it.remove();
				for(ObjectEventListener l : listeners())
					l.commandRemoved(cmd);
			}
		}
	}


	public void removeDeadObjects() {
		for(Object o : objectSet.objects()) {
			if(isDeadObject(o)) {
				objectSet.remove(o);
				for(ObjectEventListener l : listeners())
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
		addObject(obj, true);
		for(ObjectEventListener l : listeners())
			l.changeReferenceEvent(new Reference(name, referenceTypeTable.get(name), obj));
	}

	public void removeReference(String name) {
		assert referenceTable.containsKey(name);

		Reference ref = new Reference(
				name, 
				referenceTypeTable.get(name),
				referenceTable.get(name));

		referenceTable.remove(name);
		referenceTypeTable.remove(name);

		for(ObjectEventListener l : listeners())
			l.removeReferenceEvent(ref);

		for(Iterator<JavaCommand> it = activeCommands.iterator(); it.hasNext(); ) {
			JavaCommand cmd = it.next();
			if(cmd.getReference().equals(name)) {
				it.remove();
				for(ObjectEventListener l : listeners())
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
		ObjectEventListener[] listenersArray = listeners();
		for(ObjectEventListener l : listenersArray)
			l.clearAll();

		for(ObjectEventListener l : listenersArray)
			l.init();
	}





	public void execute(JavaCommand command) {
		assert command != null;

		if(command instanceof JavaCommandWithArgs)
			ExceptionHandler.INSTANCE.execute((JavaCommandWithArgs) command);
		else
			command.execute();
			
		Object target = null;
		if(command instanceof MethodInvocationCommand)
			target = ((MethodInvocationCommand) command).getTarget();
		
		if(!command.failed()) {
			addToStack(command);
			
			if(command instanceof JavaCommandWithReturn) {
				JavaCommandWithReturn cmd = (JavaCommandWithReturn) command;

				Class<?> refType = cmd.getReferenceType();
				
				if(!refType.isPrimitive() && !refType.equals(void.class)) {
					Object object = cmd.getResultingObject();
					if(object != null && cmd instanceof JavaCommandWithArgs) {
						JavaCommandWithArgs cmdArgs = (JavaCommandWithArgs) cmd;
						RuntimeException ex = verifyInvariantOnCreation(object);
						if(ex != null) {
							ExceptionHandler.INSTANCE.handleException(target, cmdArgs.getMember(), cmdArgs.getArgsText(), ex);
							return;
						}
					}
					addReference(refType, object , cmd.getReference(), true);
				}
			}
			else if(command instanceof NewReferenceCommand) {
				NewReferenceCommand cmd = (NewReferenceCommand) command;
				addReference(cmd.getReferenceType(), cmd.getResultingObject(), cmd.getReference(), true);
			}
			
			if(command instanceof ContractAware) {
				Object o = ((ContractAware) command).getObjectUnderContract();
				
				if(o != null)
					verifyInvariant(o);
			}
		}
	}


	private void addReference(Class<?> type, Object object, String name, boolean notify) {
		referenceTable.put(name, object == null? NULL_OBJECT : object);
		referenceTypeTable.put(name, type);

		if(object != null)
			addObject(object, false);

		if(notify) {
			for(ObjectEventListener l : listeners())
				l.newReferenceEvent(new Reference(name, type, object));
		}
	}





	// functions------------------------------------------------------


	//	public Object[] getObjects(Class<?> type) {
	//		assert type != null;
	//
	//		IdentityObjectSet set = new IdentityObjectSet();
	//
	//		for(Object o : objectSet.objects())
	//			if(!type.isInstance(o) && !isDeadObject(o))
	//				set.add(o);
	//
	//		return set.objects();
	//	}


	private Object getObject(String reference) {
		Object obj = referenceTable.get(reference);
		return obj == NULL_OBJECT ? null : obj;
	}






	public Map<String, Reference> getReferenceTable() {
		Map<String, Reference> table = newHashMap();
		for(String key : referenceTable.keySet()) {
			Object obj = getObject(key);
			table.put(key, new Reference(key, referenceTypeTable.get(key), obj));
		}
		return table;
	}

	public List<Reference> getCompatibleReferences(Class<?> type) {
		List<Reference> refs = new ArrayList<Reference>();

		for(String r : referenceTable.keySet()) {
			Class<?> refType = referenceTypeTable.get(r);

			if(type.isAssignableFrom(refType)) {
				Object obj = getObject(r);
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

	public boolean existsObject(Object object) {
		return objectSet.contains(object);
	}

	public boolean existsReference(String name) {
		return referenceTable.containsKey(name);
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

	public Reference getCompatibleReference(Object object, Method method) {
		if(object instanceof ContractDecorator)
			object = ((ContractDecorator<?>) object).getWrappedObject();

		Reference reference = null;
		for(String ref : referenceTable.keySet()) {
			if(referenceTable.get(ref) == object) {
				for(Method m : ClassModel.getInstance().getAllAvailableMethods(referenceTypeTable.get(ref)))
					if(ReflectionUtils.isSame(m, method))
						reference = new Reference(ref, referenceTypeTable.get(ref), object);
			}
		}
		return reference;
	}


	public static Reference getFirstReference(Object object) {
		if(object instanceof ContractDecorator)
			object = ((ContractDecorator<?>) object).getWrappedObject();

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
			Object obj = getObject(ref);
			if(obj == null) {
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
		for(ObjectEventListener l : listeners)
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

	
	


	// ------ CONTRACTS -----------------

	public static class Contract {
		public final ContractDecorator<?> decorator;
		public final Method wrappedMethod;

		private Contract(ContractDecorator<?> proxy, Method method) {
			this.decorator = proxy;
			wrappedMethod = getProxyMethod(method);
		}

		private Method getProxyMethod(Method method) {
			try {
				return decorator.getClass().getMethod(method.getName(), method.getParameterTypes());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} 
		}
	}

	public boolean hasContract(Object object, Method method) {
		return contracts.contains(object, method);
	}

	public Contract getContract(Object object, Method method) {
		if(!hasContract(object, method))
			throw new IllegalArgumentException("Object/method has no contract");

		return new Contract(contracts.get(object, method), method);
	}

	private void createContractProxies(Object object) {
		ClassModel model = ClassModel.getInstance();
		List<Method> methods = model.getAllAvailableMethods(object.getClass());
		ClassModel.getInstance().createContractProxies(contracts, object, methods);
	}

	
	public RuntimeException verifyInvariantOnCreation(Object object) {
		Set<Class<? extends ContractDecorator<?>>> contracts = ClassModel.getInstance().getContractTypes(object.getClass());
		for(Class<? extends ContractDecorator<?>> clazz : contracts) {
			ContractDecorator<?> contract = null;
			try {
				for(Constructor<?> constructor : clazz.getConstructors()) {
					Class<?>[] paramTypes = constructor.getParameterTypes();
					if(paramTypes.length == 1 && paramTypes[0].isInstance(object))
						contract = (ContractDecorator<?>) constructor.newInstance(object);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			Method invariantMethod = getInvariantMethod(contract);
			MethodInvocationCommand cmd = new MethodInvocationCommand(contract, invariantMethod);
			
			cmd.execute();
			
			if(cmd.failed()) {
				return cmd.getException();
			}
		}
		return null;

	}
	
	private boolean verifyInvariant(Object object) {
		boolean ok = true;
		for(ContractDecorator<?> proxy : new HashSet<ContractDecorator<?>>(contracts.row(object).values())) {
			Method invariantMethod = getInvariantMethod(proxy); 
			MethodInvocationCommand cmd = new MethodInvocationCommand(proxy, invariantMethod);
			if(ok)
				ok = ExceptionHandler.INSTANCE.execute(cmd, object);
		}
		return ok;
	}

	private Method getInvariantMethod(ContractDecorator<?> proxy) {
		Method invariantMethod = null;
		try {
			invariantMethod = proxy.getClass().getMethod(ContractDecorator.CHECK_INVARIANT);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return invariantMethod;
	}



}
