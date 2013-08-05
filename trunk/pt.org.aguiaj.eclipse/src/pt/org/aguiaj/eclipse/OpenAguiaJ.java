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
package pt.org.aguiaj.eclipse;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import pt.org.aguiaj.core.commands.ChangeWorkingDirCommand;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;
import pt.org.aguiaj.extensibility.AguiaJContribution;

public class OpenAguiaJ extends AbstractHandler implements IViewActionDelegate {
	private IWorkbench workbench;
	//	private IJavaProject project;

	@Override
	public void run(IAction action) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editor == null)
			return;

		IJavaProject project = null;
		IEditorInput input = editor.getEditorInput();
		IResource resource = (IResource) input.getAdapter(IResource.class);
		if(resource != null) {
			IProject proj = resource.getProject();

			try {
				project = (IJavaProject) proj.getNature(JavaCore.NATURE_ID);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		if(project == null) {
			try {
				execute(null);
				return;
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			}
		}

		try {
			IPerspectiveDescriptor pers = workbench.getActiveWorkbenchWindow().getActivePage().getPerspective();
			if(pers != null)
				Activator.setPerspective(pers.getId());

			workbench.showPerspective(AguiaJContribution.PERSPECTIVE, workbench.getActiveWorkbenchWindow());
			workbench.getActiveWorkbenchWindow().getShell().setText("AGUIA/J - Inspecting project " + project.getElementName());
			
			if(Activator.getProject() == project) {
				ReloadClassesCommand reloadCommand = new ReloadClassesCommand();
				reloadCommand.execute(null);
			}
			else {
				IPath path = project.getProject().getLocation().append(project.getOutputLocation().removeFirstSegments(1));
				ChangeWorkingDirCommand command = new ChangeWorkingDirCommand();
				command.setDirectory(path);
				command.execute(null);
				Activator.setProject(project);
			}
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {			
				page.showView(AguiaJContribution.CLASSES_VIEW);			
			} 
			catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		//		if(selection instanceof IStructuredSelection) {
		//			Object obj = ((IStructuredSelection) selection).getFirstElement();
		//			if(obj instanceof IJavaProject) {
		//				project = (IJavaProject) obj;
		//			}
		//			else if(obj instanceof IProject) {
		//				IProjectNature nature = null;
		//				try {
		//					nature = ((IProject) obj).getNature(JavaCore.NATURE_ID);
		//				} catch (CoreException e) {
		//					e.printStackTrace();
		//				}
		//				if(nature != null)
		//					project = (IJavaProject) nature;
		//			}
		//			else if (obj instanceof IJavaElement) {				
		//				project = ((IJavaElement) obj).getJavaProject();				
		//			}
		//			else if (obj instanceof IResource) {
		//				IProjectNature nature = null;
		//				try {
		//					nature = ((IResource) obj).getProject().getNature(JavaCore.NATURE_ID);
		//				} catch (CoreException e) {
		//					e.printStackTrace();
		//				}
		//				if(nature != null)
		//					project = (IJavaProject) nature;
		//			}			
		//		}	
	}

	@Override
	public void init(IViewPart view) {		
		workbench = view.getSite().getWorkbenchWindow().getWorkbench();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(AguiaJContribution.PERSPECTIVE, workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

}
