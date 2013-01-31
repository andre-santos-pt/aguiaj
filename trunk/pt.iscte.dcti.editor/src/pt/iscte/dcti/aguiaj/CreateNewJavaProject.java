package pt.iscte.dcti.aguiaj;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class CreateNewJavaProject implements IViewActionDelegate {

	@Override
	public void run(IAction action) {
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Set<String> existing = new HashSet<String>();
		for(IProject proj : root.getProjects())
			existing.add(proj.getName());

		NameDialog dialog = new NameDialog(Display.getDefault().getActiveShell(), "Project", existing, false, true);
		dialog.open();
		String name = dialog.getName();
		if(name == null)
			return;
		
		IProject project = root.getProject(name);
		try {
			project.create(progressMonitor);
			project.open(progressMonitor);

			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = JavaCore.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, progressMonitor);

			IJavaProject javaProject = JavaCore.create(project);
			Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();

			entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
			entries.add(JavaRuntime.getDefaultJREContainerEntry());
			
			IVMInstall vmInstall= JavaRuntime.getDefaultVMInstall();

			LibraryLocation[] locations= JavaRuntime.getLibraryLocations(vmInstall);

			for (LibraryLocation element : locations)
				entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
			
//				if(element.getSystemLibraryPath().toString().indexOf("rt.jar") != -1)
//				entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
			//				entries.add(JavaCore.newProjectEntry(element.getSystemLibraryPath()));

			javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), progressMonitor);
			
//			Set entries = new HashSet();
//			entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
//			entries.add(JavaRuntime.getDefaultJREContainerEntry());
//			javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), progressMonitor);


		}
		catch(CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {

	}

}
