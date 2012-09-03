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
package pt.org.aguiaj.eclipse;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.WorkbenchException;

public class OpenJavaPerspective implements IViewActionDelegate {
	private IWorkbench workbench;
	
	@Override
	public void run(IAction action) {
		String pers = Activator.getPerspective();
		if(pers == null)
			pers = JavaUI.ID_PERSPECTIVE;

		try {			
			workbench.showPerspective(pers, workbench.getActiveWorkbenchWindow());						
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		workbench = view.getSite().getWorkbenchWindow().getWorkbench();
	}

}
