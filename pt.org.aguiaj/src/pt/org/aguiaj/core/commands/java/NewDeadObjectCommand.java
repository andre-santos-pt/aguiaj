/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.commands.java;



import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.core.commands.Command;
import pt.org.aguiaj.objects.ObjectsView;


public class NewDeadObjectCommand implements Command {

	private Object object;
	
	public NewDeadObjectCommand(Object object) {
		this.object = object;
	}

	
	public void execute() {
		ObjectsView.getInstance().addDeadObjectWidget(object);
	}
	
}
