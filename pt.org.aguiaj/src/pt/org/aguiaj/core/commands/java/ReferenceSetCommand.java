/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.commands.java;



public class ReferenceSetCommand extends JavaCommandWithReturn {

	private String refId;
	private Object object;
	private Class<?> refType;
	private String targetRef;

	public ReferenceSetCommand(String refId, Class<?> refType, Object object, String targetRef) {
		assert refId != null;
		assert refType != null;
		
		this.refId = refId;
		this.refType = refType;
		this.object = object;
		this.targetRef = targetRef;
	}


	
	public String getJavaInstruction() {
		return refId + " = " + targetRef; 
	}

	
	public void execute() {
//		ObjectsView.getInstance().addReference(refType, refId, object);
	}

	
	public String getReference() {
		return refId;
	}

	
	public Object getResultingObject() {
		return object;
	}

	@Override
	public Class<?> getReferenceType() {
		return refType;
	}
}
