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

import java.util.Collection;


public class RemoveObjectsCommand implements Command {

	private Collection<Object> objects;
	
	public RemoveObjectsCommand(Collection<Object> objects) {
		this.objects = objects;
	}
	
	
	public void execute() {
		for(Object object : objects.toArray())
			new RemoveObjectCommand(object).execute();
		
		HistoryView.getInstance().clear();
		System.gc();
	}

}
