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

import org.eclipse.core.commands.ExecutionException;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;

public class ImportCommand extends JavaCommand {
	private String packageName;
	
	public ImportCommand(String packageName) {
		this.packageName = packageName;
	}
	
	@Override
	public void execute() {
		ClassModel.getInstance().activatePackage(packageName);
		ReloadClassesCommand reload = new ReloadClassesCommand();
		try {
			reload.execute(null);
		} catch (ExecutionException e) {						
			e.printStackTrace();
		}
	}

	@Override
	public String getJavaInstruction() {
		return "import " + packageName;
	}

}
