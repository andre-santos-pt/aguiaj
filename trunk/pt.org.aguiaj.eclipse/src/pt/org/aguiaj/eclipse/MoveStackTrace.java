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

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.TraceLocation;

public abstract class MoveStackTrace implements IViewActionDelegate {
//	private IWorkbench workbench;
	
	private boolean backward;
	
	public MoveStackTrace(boolean backward) {
		this.backward = backward;
	}
	
	@Override
	public void run(IAction action) {
		IJavaProject project = Activator.getProject();
		
		if(backward)
			Activator.getInstance().movePreviousLocation();
		else		
			Activator.getInstance().moveNextLocation();
	
		TraceLocation loc = Activator.getInstance().getLocation();
		
		if(loc == null)
			return;
		
		IJavaElement element = null;
		try {
			element = project.findElement(new Path(loc.fileName));
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		if(element == null)
			return;
		
		IFile file = (IFile) element.getResource();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 

		String pers = Activator.getPerspective();
		if(pers == null)
			pers = JavaUI.ID_PERSPECTIVE;

		try {			
			PlatformUI.getWorkbench().showPerspective(pers, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		
		try {
			IMarker marker = file.createMarker(IMarker.TEXT);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(IMarker.LINE_NUMBER, new Integer(loc.line));
			marker.setAttributes(map);
			IDE.openEditor(page, marker); 
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
//		workbench = view.getSite().getWorkbenchWindow().getWorkbench();
	}

}
