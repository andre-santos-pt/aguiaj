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
package pt.org.aguiaj.core.commands.java;

import pt.org.aguiaj.objects.ObjectModel;



public class ReferenceSetCommand extends JavaCommand {

	private String name;
	private Object object;
	private Class<?> refType;
	private String targetRef;

	public ReferenceSetCommand(String refId, Class<?> refType, Object object, String targetRef) {
		assert refId != null;
		assert refType != null;
		
		this.name = refId;
		this.refType = refType;
		this.object = object;
		this.targetRef = targetRef;
	}


	@Override
	public String getJavaInstruction() {
		return name + " = " + targetRef; 
	}

	@Override
	public void execute() {
		ObjectModel.getInstance().changeReference(name, object);
	}


	@Override
	public String getReference() {
		return name;
	}
}
