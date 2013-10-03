package org.eclipselabs.reflectionutils.wrappers;

import java.util.Collection;

public class RInterface extends TypeR {

	public RInterface(Class<?> interfacce) {
		super(interfacce);
		if(!interfacce.isInterface())
			throw new IllegalArgumentException("not an interface");	
	}
	
	public Collection<RInterface> getAllSuperInterfaces() {
		return null;
	}
	
	
}
