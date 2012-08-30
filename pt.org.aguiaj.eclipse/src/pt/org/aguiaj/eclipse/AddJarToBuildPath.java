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
package pt.org.aguiaj.eclipse;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import pt.org.aguiaj.extensibility.AguiaJHelper;


public class AddJarToBuildPath implements IViewActionDelegate {	
	
	@Override
	public void run(IAction action) {
		IJavaProject project = Activator.getProject();

		if(project == null) {
			MessageBox message = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_WARNING);
			message.setText("Import plugin JAR");
			message.setMessage("No project was opened in AGUIA/J.");
			message.open();
			return;
		}
		
		String plugin = AguiaJHelper.getActivePlugin();
		if(plugin == null) {
			MessageBox message = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_WARNING);
			message.setText("Import plugin JAR");
			message.setMessage("Please select the package of the plugin you want to import.");
			message.open();
			return;
		}
		
		String jarLocation = AguiaJHelper.getPluginJarLocation(plugin);

		try {
			MessageBox message = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_INFORMATION);
			message.setText("Import plugin JAR");
			
			IClasspathEntry[] entries = project.getRawClasspath();
			boolean exists = false;
			for(IClasspathEntry entry : entries) {
				String abs = entry.getPath().toOSString();
				if(abs.equals(jarLocation)) {
					message.setMessage("Project " + project.getElementName() + " already imported the plugin.");
					exists = true;
				}					
			}

			if(!exists) {
				IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
				System.arraycopy(entries, 0, newEntries, 0, entries.length);						
				newEntries[entries.length] = JavaCore.newLibraryEntry(new Path(jarLocation), null, null, true);						
				project.setRawClasspath(newEntries, null);
				message.setMessage("Plugin imported into project " + project.getElementName() + ".");
			}
			
			message.open();
		}
		catch (JavaModelException ex) {
			ex.printStackTrace();
		}			
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {

	}

}
