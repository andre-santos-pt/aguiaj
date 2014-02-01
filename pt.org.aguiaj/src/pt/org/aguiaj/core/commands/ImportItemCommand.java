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
package pt.org.aguiaj.core.commands;

import static pt.org.aguiaj.extensibility.AguiaJContribution.EXTENSION_IMPORT_ITEM;
import static pt.org.aguiaj.extensibility.AguiaJContribution.IMPORT_ITEM_CLASS;
import static pt.org.aguiaj.extensibility.AguiaJContribution.IMPORT_ITEM_EXTENSION;
import static pt.org.aguiaj.extensibility.AguiaJContribution.IMPORT_ITEM_FILE_EXTENSION;
import static pt.org.aguiaj.extensibility.AguiaJContribution.IMPORT_ITEM_NAME;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.ImportItemProvider;

public class ImportItemCommand extends AbstractHandler {

	private static class ImportItemProviderWrapper implements ImportItemProvider {

		final ImportItemProvider provider;
		final String name;
		final String extensions;
		
		public ImportItemProviderWrapper(ImportItemProvider provider, String name, String extensions) {
			this.provider = provider;
			this.name = name;
			this.extensions = extensions;
		}
		
		@Override
		public Class<?> getType() {
			return provider.getType();
		}

		@Override
		public String getInstruction(String filePath) {
			return provider.getInstruction(filePath);
		}
		
		@Override
		public String toString() {
			return name;
		}
	}



	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = Display.getDefault().getActiveShell();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(shell, new LabelProvider());
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_IMPORT_ITEM);
		List<ImportItemProvider> prov = new ArrayList<ImportItemProvider>();
		
		for(IConfigurationElement e : config) {

//			String pluginName = e.getDeclaringExtension().getLabel();			
//			String pluginID = e.getContributor().getName();
			try {
				ImportItemProvider p = (ImportItemProvider) e.createExecutableExtension(IMPORT_ITEM_CLASS);
				
				String extensions = handleExtensions(e.getChildren(IMPORT_ITEM_FILE_EXTENSION));
				prov.add(new ImportItemProviderWrapper(p, e.getAttribute(IMPORT_ITEM_NAME), extensions));
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
		dialog.setElements(prov.toArray());
		dialog.setTitle("Import...");
		if (dialog.open() == Window.OK && dialog.getResult().length == 1) {
			ImportItemProviderWrapper provider = (ImportItemProviderWrapper) dialog.getResult()[0];
			FileDialog fd = new FileDialog(shell, SWT.OPEN);
			fd.setText("Open");
			String[] filterExt = { provider.extensions };
			fd.setFilterExtensions(filterExt);			
			String path = fd.open();
			
			if(path != null)
				AguiaJHelper.executeJavaInstruction(provider.getInstruction(path));					
		}
		return null;
	}



	private String handleExtensions(IConfigurationElement[] fileExtensions) {
		String ext = fileExtensions.length == 0 ? "*.*" : "";
		for(IConfigurationElement e : fileExtensions) {
			if(!ext.isEmpty())
				ext += ";";
			ext += "*." + e.getAttribute(IMPORT_ITEM_EXTENSION);
		}
		return ext;
	}
}
