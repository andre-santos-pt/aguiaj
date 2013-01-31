package pt.iscte.dcti.aguiaj;


import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class CreateNewFolder implements IViewActionDelegate {
	private IContainer container;
	
//	private IJavaProject project;
//	private IPackageFragment parentFolder;

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
		NameDialog dialog = new NameDialog(Display.getDefault().getActiveShell(), "newfolder", existingNames, false, false);
		dialog.open();
		String name = dialog.getName();
		if(name != null) {
			IFolder newFolder = container.getFolder(new Path(name));
			try {
				newFolder.create(true, true, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
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
