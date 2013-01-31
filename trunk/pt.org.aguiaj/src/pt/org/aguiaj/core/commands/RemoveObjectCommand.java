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
package pt.org.aguiaj.core.commands;

import pt.org.aguiaj.objects.ObjectModel;

public class RemoveObjectCommand extends ObjectModelCommand {

	private Object object;
	
	public RemoveObjectCommand(Object object) {
		this.object = object;
	}

	@Override
	protected void execute(ObjectModel model) {
		model.removeObject(object);
		
	}
}
