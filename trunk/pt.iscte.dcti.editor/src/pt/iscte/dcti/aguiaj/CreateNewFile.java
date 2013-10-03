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
package pt.iscte.dcti.aguiaj;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class CreateNewFile implements IViewActionDelegate {
	private IContainer container;

	@Override
	public void run(IAction action) {
		Set<String> existingNames = new HashSet<String>();
		try {
			for(IResource element : container.members()) {
				existingNames.add(element.getName());
			}
		} catch (CoreException e1) {
			e1.printStackTrace();
		}
		TextDialog dialog = new TextDialog(Display.getDefault().getActiveShell(), "Name", "JavaFile.java", existingNames, '_', '.');
		dialog.open();
		String name = dialog.getName();
		IFile newFile = null;
		if(name != null) {
			newFile = container.getFile(new Path(name));
			try {
				newFile.create(new ByteArrayInputStream(new byte[0]), true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	
		if(newFile == null)
			return;
		
		try {
			IMarker marker = newFile.createMarker(IMarker.TEXT);
			IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() , marker);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} 
		PackageExplorerPart part = PackageExplorerPart.getFromActivePerspective();
		part.selectAndReveal(newFile);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			if(obj instanceof IJavaProject) {
				container = ((IJavaProject) obj).getProject();
			}
			else if(obj instanceof IProject) {
				container = (IProject) obj;
			}
			else if(obj instanceof IPackageFragment) {
				try {
					IResource resource = ((IPackageFragment) obj).getUnderlyingResource();
					if(resource instanceof IFolder)
						container = (IFolder) resource;
					else if(resource instanceof IProject)
						container = (IProject) resource;
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
			else if(obj instanceof IFolder) {
				container = (IFolder) obj;
			}
		}
	}

	@Override
	public void init(IViewPart view) {

	}

}
