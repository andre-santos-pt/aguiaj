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
package pt.org.aguiaj.core;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.SWTUtils;
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

	private static String anchor(Method method) {
		return method.getName() + 
		"(" + concatParams(method.getParameterTypes()) + ")";
	}

	private static String anchor(Field field) {
		return field.getName();
	}

	private static String anchor(Constructor<?> constructor) {
		return constructor.getDeclaringClass().getSimpleName() + 
		"(" + concatParams(constructor.getParameterTypes()) + ")";
	}

	private static String concatParams(Class<?>[] params) {
		String list = "";
		for(int i = 0; i < params.length; i++) {
			if(i != 0)
				list += ", ";

			if(params[i].isPrimitive())
				list += params[i].getSimpleName();
			else if(params[i].isArray())
				list += componentType(params[i]) + arrayBrackets(params[i]);
			else
				list += params[i].getName();
		}
		return list;
	}

	private static String componentType(Class<?> type) {
		if(!type.isArray())
			return type.getName();
		else
			return componentType(type.getComponentType());
	}

	private static String arrayBrackets(Class<?> type) {
		if(type.isArray() && !type.getComponentType().isArray())
			return "[]";
		else
			return "[]" + arrayBrackets(type.getComponentType());
	}

	public void addDocumentationSupport(Control control, Class<?> clazz) {				
		addSupport(control, clazz, "");
	}

	public void addDocumentationSupport(Control control, Field field) {				
		addSupport(control, field.getDeclaringClass(), anchor(field));
	}

	public void addDocumentationSupport(Control control, Constructor<?> constructor) {
		addSupport(control, 
				constructor.getDeclaringClass(), 
				anchor(constructor));

	}

	public void addDocumentationSupport(Control control, Method method) {
		final Class<?> clazz = method.getDeclaringClass();
		if(isPluginWithDocumentation(clazz))
			addSupport(control, clazz, anchor(method));
	}

	private void addSupport(Control control, final Class<?> clazz, final String anchor) {
		addHover(control, clazz, anchor);
	}

	private static boolean isPluginWithDocumentation(Class<?> clazz) {
		if(!ClassModel.getInstance().isPluginClass(clazz))
			return false;

		String pluginId = ClassModel.getInstance().getPluginId(clazz);
		return Platform.getBundle(pluginId).getEntry(AguiaJParam.DOC_ROOT.getString()) != null;
	}


	private void addHover(final Control control, final Class<?> clazz, final String anchor) {
		if(isPluginWithDocumentation(clazz)) {
			control.addMouseTrackListener(new MouseTrackListener() {
				@Override
				public void mouseHover(MouseEvent e) {
					DocumentationView.getInstance().load(clazz, anchor, control);					
				}

				@Override
				public void mouseExit(MouseEvent e) {

				}

				@Override
				public void mouseEnter(MouseEvent e) {

				}
			});
		}
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}
}
