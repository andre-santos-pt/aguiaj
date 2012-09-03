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

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.objects.ObjectsView;

public class RemoveReferenceCommand implements Command {

	private String referenceName;
	
	public RemoveReferenceCommand(String referenceName) {
		this.referenceName = referenceName;
	}
	
	public String referenceName() {
		return referenceName;
	}
	
	public void execute() {
		if(ObjectModel.aspectOf().isNullReference(referenceName))			
			ObjectsView.getInstance().removeNullReference(referenceName);
		else
			ObjectsView.getInstance().removeReference(referenceName);
	}

}
