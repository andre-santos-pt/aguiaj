package org.eclipselabs.javainterpreter;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleContext implements Context {

	private Map<String,Object> refTable;
	private Map<String,Class<?>> refTypes;
	private Map<String,Class<?>> classes;
	private Set<Class<?>> mainClassSet;

	public SimpleContext(Class<?> mainClass, Class<?> ... classes) {
		mainClassSet = new HashSet<Class<?>>(1);
		if(mainClass != null)
			mainClassSet.add(mainClass);
		
		this.classes = new HashMap<String, Class<?>>();
		for(Class<?> c : classes)
			this.classes.put(c.getSimpleName(), c);
		
		refTable = new HashMap<String, Object>();
		refTypes = new HashMap<String, Class<?>>();
	}

	@Override
	public boolean isClassAvailable(String name) {
		return classes.containsKey(name);
	}
	
	@Override
	public Class<?> getClass(String name) {
		return classes.get(name);
	}
	
	@Override
	public boolean existsReference(String name) {
		return refTable.containsKey(name);
	}
	
	@Override
	public Object getObject(String referenceName) {
		return refTable.get(referenceName);
	}
	
	@Override
	public Set<Class<?>> getImplicitClasses() {
		return Collections.unmodifiableSet(mainClassSet);
	}

	@Override
	public void addReference(Class<?> type, String name, Object object) {
		refTable.put(name, object);
		refTypes.put(name, type);
	}

	@Override
	public Class<?> referenceType(String name) {
		return refTypes.get(name);
	}
}
