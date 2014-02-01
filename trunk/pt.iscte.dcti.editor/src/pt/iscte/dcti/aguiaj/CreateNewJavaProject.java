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
package pt.iscte.dcti.aguiaj;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import pt.org.aguiaj.extensibility.AguiaJHelper;

public class CreateNewJavaProject implements IViewActionDelegate {

	@Override
	public void run(IAction action) {
		IProgressMonitor monitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Set<String> existing = new HashSet<String>();
		for(IProject proj : root.getProjects())
			existing.add(proj.getName());

		TextDialog dialog = new TextDialog(Display.getDefault().getActiveShell(), "Name", "Project", existing, '-', '_', '.');
		dialog.open();
		String name = dialog.getName();
		if(name == null)
			return;

		IProject project = root.getProject(name);
		try {
			monitor.beginTask("create project", 1);
			project.create(monitor);
			monitor.worked(1);
			
			monitor.beginTask("initialize project", 5);
			project.open(monitor);
			monitor.worked(1);
			
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 2];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = JavaCore.NATURE_ID;
			newNatures[natures.length + 1] = "org.eclipse.pde.PluginNature";
			description.setNatureIds(newNatures);
			monitor.worked(1);
			
			final ICommand java = description.newCommand();
			java.setBuilderName(JavaCore.BUILDER_ID);

			final ICommand manifest = description.newCommand();
			manifest.setBuilderName("org.eclipse.pde.ManifestBuilder");

			description.setBuildSpec(new ICommand[] { java, manifest});
			project.setDescription(description, monitor);

			IJavaProject javaProject = JavaCore.create(project);
			List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
			final IClasspathEntry srcClasspathEntry = JavaCore.newSourceEntry(project.getFullPath());

			entries.add(srcClasspathEntry);
			entries.add(JavaRuntime.getDefaultJREContainerEntry());
			entries.add(JavaCore.newContainerEntry(new Path("org.eclipse.pde.core.requiredPlugins")));
			javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), monitor);
			
			Set<String> bundles = new HashSet<String>(AguiaJHelper.getPluginIds());
			
//			bundles.add("pt.org.aguiaj");
//			bundles.add("pt.org.aguiaj.draw");
//			bundles.add("pt.org.aguiaj.draw.examples");

			List<String> list = new ArrayList<String>();
			createManifest(name, bundles, list, null, project);
			list.add("/");
			createBuildProps(null, project, list);
		}
		catch(CoreException e) {
			e.printStackTrace();
		}


		//		Set<IClasspathEntry> entries = new HashSet<IClasspathEntry>();
		//
		//		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		//		entries.add(JavaRuntime.getDefaultJREContainerEntry());
		//		
		//		IVMInstall vmInstall= JavaRuntime.getDefaultVMInstall();
		//		
		//		LibraryLocation[] locations= JavaRuntime.getLibraryLocations(vmInstall);
		//
		//		for (LibraryLocation element : locations)
		//			entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		//		
		//		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), progressMonitor);

		//		Set entries = new HashSet();
		//		entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
		//		entries.add(JavaRuntime.getDefaultJREContainerEntry());
		//		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), progressMonitor);
	}

	private static String findJavaExecutionEnvironment() {
		for(IExecutionEnvironment env : JavaRuntime.getExecutionEnvironmentsManager().getExecutionEnvironments())
			if(env.isStrictlyCompatible(JavaRuntime.getDefaultVMInstall()))
				return env.getId();

		return "JavaSE-1.6"; 
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {

	}

	public static IFile createFile(final String name, final IContainer container, final String content,
			final IProgressMonitor progressMonitor) {
		final IFile file = container.getFile(new Path(name));
		try {
			final InputStream stream = new ByteArrayInputStream(content.getBytes(file.getCharset()));
			if (file.exists()) {
				file.setContents(stream, true, true, progressMonitor);
			}
			else {
				file.create(stream, true, progressMonitor);
			}
			stream.close();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		return file;
	}

	public static IFile createFile(final String name, final IContainer container, final String content,
			final String charSet, final IProgressMonitor progressMonitor) throws CoreException {
		final IFile file = createFile(name, container, content, progressMonitor);
		if (file != null && charSet != null) {
			file.setCharset(charSet, progressMonitor);
		}

		return file;
	}

	private static void createBuildProps(final IProgressMonitor progressMonitor, final IProject project,
			final List<String> srcFolders) {
		final StringBuilder bpContent = new StringBuilder("source.. = ");
		for (final Iterator<String> iterator = srcFolders.iterator(); iterator.hasNext();) {
			bpContent.append(iterator.next()).append('/');
			if (iterator.hasNext()) {
				bpContent.append(",");
			}
		}
		bpContent.append("\n");
		bpContent.append("bin.includes = META-INF/,.\n");
		createFile("build.properties", project, bpContent.toString(), progressMonitor);
	}

	private static void createManifest(final String projectName, final Set<String> requiredBundles,
			final List<String> exportedPackages, final IProgressMonitor progressMonitor, final IProject project)
					throws CoreException {
		final StringBuilder maniContent = new StringBuilder("Manifest-Version: 1.0\n");
		maniContent.append("Bundle-ManifestVersion: 2\n");
		maniContent.append("Bundle-Name: " + projectName + "\n");
		maniContent.append("Bundle-SymbolicName: " + projectName + "; singleton:=true\n");
		maniContent.append("Bundle-Version: 1.0.0\n");
		// maniContent.append("Bundle-Localization: plugin\n");
		maniContent.append("Require-Bundle: ");
		Iterator<String> it = requiredBundles.iterator();
		while (it.hasNext()) {
			String entry = it.next();
			maniContent.append(" " + entry);
			if(it.hasNext())
				maniContent.append(",\n");
		}
		maniContent.append("\n");

		if (exportedPackages != null && !exportedPackages.isEmpty()) {
			maniContent.append("Require-Bundle: " + exportedPackages.get(0));
			for (int i = 1, x = exportedPackages.size(); i < x; i++) {
				maniContent.append(",\n " + exportedPackages.get(i));
			}
			maniContent.append("\n");
		}

		maniContent.append("Bundle-RequiredExecutionEnvironment: " + findJavaExecutionEnvironment() + "\r\n");

		final IFolder metaInf = project.getFolder("META-INF");
		metaInf.create(false, true, null);
		createFile("MANIFEST.MF", metaInf, maniContent.toString(), progressMonitor);
	}

	/**
	 * @param name
	 *            of the destination file
	 * @param container
	 *            directory containing the the destination file
	 * @param contentUrl
	 *            Url pointing to the src of the content
	 * @param progressMonitor
	 *            used to interact with and show the user the current operation
	 *            status
	 * @return
	 */
	public static IFile createFile(final String name, final IContainer container, final URL contentUrl,
			final IProgressMonitor progressMonitor) {

		final IFile file = container.getFile(new Path(name));
		InputStream inputStream = null;
		try {
			inputStream = contentUrl.openStream();
			if (file.exists()) {
				file.setContents(inputStream, true, true, progressMonitor);
			}
			else {
				file.create(inputStream, true, progressMonitor);
			}
			inputStream.close();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
		finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				}
				catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		progressMonitor.worked(1);

		return file;
	}
}
