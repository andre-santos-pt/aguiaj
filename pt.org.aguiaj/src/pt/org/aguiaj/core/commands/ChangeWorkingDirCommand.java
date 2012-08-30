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
package pt.org.aguiaj.core.commands;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.core.AguiaJActivator;

public class ChangeWorkingDirCommand extends AbstractHandler {

	private IPath workingDir;

	public void setDirectory(IPath path) {
		workingDir = path;
	}	

	public Object execute(ExecutionEvent event) throws ExecutionException {

		if(workingDir == null) {
			WorkingDirectoryDialog dia = new WorkingDirectoryDialog(Display.getDefault().getActiveShell(), AguiaJActivator.getDefault().getWorkingDirectory());
			workingDir = dia.open();
		}
		
		RemoveObjectsCommand removeObjectsCommand = new RemoveObjectsCommand(Arrays.asList(ObjectModel.aspectOf().getAllObjects()));
		removeObjectsCommand.execute();

		ReloadClassesCommand reloadCommand = new ReloadClassesCommand();
		reloadCommand.setWorkingDir(workingDir);
		reloadCommand.execute(null);

		return null;
	}


	public static Action createAction() {
		return new Action("Change working directory") {
			public void run() {
				try {
					new ChangeWorkingDirCommand().execute(null);
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
	}
}
