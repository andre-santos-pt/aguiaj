/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.eclipse;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;

public class EclipseUtil {

	//private void gotoLine(TraceLocation loc) {
	public static void gotoLine(String path, int line) {
		IJavaElement element = null;
		try {
			element = Activator.getProject().findElement(new Path(path));
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
			map.put(IMarker.LINE_NUMBER, new Integer(line));
			marker.setAttributes(map);
			IDE.openEditor(page, marker); 
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
