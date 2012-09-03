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
package pt.org.aguiaj.core.commands;

import pt.org.aguiaj.objects.ObjectsView;

public class RemoveObjectCommand implements Command {

	private Object object;
	
	public RemoveObjectCommand(Object object) {
		this.object = object;
	}
	
	
	public void execute() {
		ObjectsView.getInstance().remove(object);
	}

	public Object getObject() {
		return object;
	}
}
