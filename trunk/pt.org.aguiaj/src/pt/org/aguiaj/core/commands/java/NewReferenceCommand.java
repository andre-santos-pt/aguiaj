/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.core.commands.java;



import pt.org.aguiaj.aspects.ObjectModel;


public class NewReferenceCommand extends JavaCommandWithReturn {

	private Class<?> refType;
	private Object object;
	private String source;
	private String reference;
	
	public NewReferenceCommand(Class<?> refType, Object object, String source) {
		this(refType, object, source, ObjectModel.getInstance().nextReference(refType));
	}

	public NewReferenceCommand(Class<?> refType, Object object, String source, String reference) {
		assert refType != null;
		assert source != null;
		assert reference != null;
		
		this.refType = refType;
		this.object = object;
		this.source = source;
		this.reference = reference;
	}
	
	public String getJavaInstruction() {
		return refType.getSimpleName() + " " + reference + " = " + source; 
	}
	
	public void execute() {

	}
	
	public String getReference() {
		return reference;
	}
	
	
	public Object getResultingObject() {
		return object;
	}
	
	@Override
	public Class<?> getReferenceType() {
		return refType;
	}
}
