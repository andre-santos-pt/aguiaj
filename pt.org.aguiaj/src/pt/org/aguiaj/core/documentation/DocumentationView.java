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
package pt.org.aguiaj.core.documentation;


import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.extensibility.AguiaJContribution;

public class DocumentationView extends ViewPart {
	public static class Open extends AbstractHandler {
		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			SWTUtils.showView(AguiaJContribution.DOCUMENTATION_VIEW);
			return null;
		}	
	}

	private static DocumentationView instance;

	private Browser browser;	

	public DocumentationView() {
		instance = this;
	}

	public static DocumentationView getInstance() {
		if(instance == null)
			SWTUtils.showView(AguiaJContribution.DOCUMENTATION_VIEW);

		return instance;
	}

	@Override
	public void dispose() {
		super.dispose();
		instance = null;
	}

	public static void activate() {
		SWTUtils.showView(AguiaJContribution.DOCUMENTATION_VIEW);
	}

	@Override
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.NONE);
				
		final Listener listener = new Listener() {
			public void handleEvent(Event e) {
				Object data = e.widget.getData();
				if(data instanceof ControlAnchor)
					load(((ControlAnchor) data).clazz, ((ControlAnchor) data).anchor, (Control) e.widget);
			}
		};
		
		Display.getDefault().addFilter(SWT.MouseEnter, listener);
		
		browser.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				Display.getDefault().removeFilter(SWT.MouseEnter, listener);
			}
		});
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	public void load(String file) {
		browser.setUrl("file://" + file);
	}


	public void load(Class<?> clazz, String anchor, Control control) {
		String pluginId = ClassModel.getInstance().getPluginId(clazz);
		IPath path = new Path(AguiaJParam.DOC_ROOT.getString());

		for(String frag : clazz.getPackage().getName().split("\\."))
			path = path.append(frag);

		path = path.append(clazz.getSimpleName()).addFileExtension("html");

		URL url = Platform.getBundle(pluginId).getEntry(path.toString());
		if(url != null) {
			URL fileurl = null;
			try {
				fileurl = FileLocator.resolve(url);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			if(fileurl != null) {
				String fileWithlabel = fileurl.getFile();

				File file = new File(fileWithlabel);
				if(file.exists()) {

					if(!anchor.isEmpty())
						fileWithlabel += "#" + anchor;

					load(fileWithlabel);
				}
			}
		}
	}

}
