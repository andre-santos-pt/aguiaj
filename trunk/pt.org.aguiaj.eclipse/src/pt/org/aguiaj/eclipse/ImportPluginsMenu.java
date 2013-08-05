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

import static pt.org.aguiaj.extensibility.AguiaJContribution.EXTENSION_OBJECT_WIDGET;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import pt.org.aguiaj.extensibility.AguiaJContribution;
import pt.org.aguiaj.extensibility.AguiaJHelper;

public class ImportPluginsMenu extends ContributionItem {
	public ImportPluginsMenu() {
		
	}

	public ImportPluginsMenu(String id) {
		super(id);
	}

	@Override
	public boolean isVisible() {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection(JavaUI.ID_PACKAGES);
		IJavaProject project = null;
		if(selection instanceof IStructuredSelection) {
			Object sel = ((IStructuredSelection) selection).getFirstElement();
			if(sel instanceof IJavaProject)
				project = (IJavaProject) sel;
		}
		return project != null;
	}

	@Override
	public void fill(Menu menu, int index) {
		MenuItem submenu = new MenuItem(menu, SWT.CASCADE);
		submenu.setText("Import AGUIA/J plugin classes");
		String path = null;
		try {
			path =  FileLocator.toFileURL(Activator.getContext().getBundle().getEntry("icons/eagle-icon-16.png")).getPath();
		} catch (IOException e1) {			
			e1.printStackTrace();
		}
		if(path != null) {
			Image img = new Image(Display.getDefault(), path);		
			submenu.setImage(img);
			img.dispose();
		}
		Menu items = new Menu(menu);
		submenu.setMenu(items);

		IConfigurationElement[] config = 
			Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_OBJECT_WIDGET);

		Set<String> plugins = new HashSet<String>();
		Map<String, String> pluginIDs = new HashMap<String, String>();

		for (final IConfigurationElement e : config) {
			String pluginName = e.getDeclaringExtension().getLabel();			
			final String pluginID = e.getContributor().getName();
				
			if(!plugins.contains(pluginName)) {
				boolean hasNonStandardClasses = false;
				for(IConfigurationElement c : e.getChildren("class")) {
//					for(IConfigurationElement clazz : group.getChildren("class")) {
						if(!c.getAttribute(AguiaJContribution.OBJECT_WIDGET_ID).startsWith("java.lang"))
							hasNonStandardClasses = true;
//					}
				}
				if(hasNonStandardClasses) {
					plugins.add(pluginName);
					pluginIDs.put(pluginName, pluginID);
				}
			}
		}

		for(final Entry<String, String> entry : pluginIDs.entrySet()) {
			MenuItem item = new MenuItem(items, SWT.PUSH);
			item.setText(entry.getKey());
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection(JavaUI.ID_PACKAGES);
					IJavaProject project = null;
					if(selection instanceof IStructuredSelection) {
						Object sel = ((IStructuredSelection) selection).getFirstElement();
						if(sel instanceof IJavaProject)
							project = (IJavaProject) sel;
					}

					if(project == null) {
						MessageBox message = new MessageBox(Display.getDefault().getActiveShell(), SWT.ICON_INFORMATION);
						message.setText("Import plugin JAR");
						message.setMessage("No project is selected");
						message.open();
						return;
					}


					String jarLocation = AguiaJHelper.getPluginJarLocation(entry.getValue());

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
							
							Path root = new Path(jarLocation);
							
							IClasspathAttribute atts[] = new IClasspathAttribute[] {
							    JavaCore.newClasspathAttribute("javadoc_location", root.append("doc").toFile().toURI().toString()),
							};
							IClasspathEntry entry = JavaCore.newLibraryEntry(root, root.append("src"), null, null, atts, false);
							
//							IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(jarLocation), new Path(jarLocation).append("src"), null, true);
							newEntries[entries.length] = entry;				
							project.setRawClasspath(newEntries, null);
							message.setMessage("Plugin classes imported into project " + project.getElementName() + ".");
						}

						message.open();
					}
					catch (JavaModelException ex) {
						ex.printStackTrace();
					}			
				}
			});
		}
	}
}
