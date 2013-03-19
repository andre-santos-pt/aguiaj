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



import pt.org.aguiaj.core.commands.ObjectModelCommand;
import pt.org.aguiaj.objects.ObjectModel;


public class NewDeadObjectCommand extends ObjectModelCommand {

	private Object object;
	
	public NewDeadObjectCommand(Object object) {
		this.object = object;
	}

	@Override
	protected void execute(ObjectModel model) {
		model.addObject(object, true);		
	}
	
}
