package org.eclipselabs.javainterpreter;

import java.util.List;
import java.util.Set;

public interface Context {

	Set<Class<?>> getImplicitClasses();
	
	boolean isClassAvailable(String name);
	
	Class<?> getClass(String name);
	
	boolean existsReference(String name);
	
	Class<?> referenceType(String name);
	
	Object getObject(String referenceName);

	void addReference(Class<?> type, String name, Object object);

	
}
