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
package pt.org.aguiaj.rcp;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import pt.org.aguiaj.extensibility.AguiaJHelper;

public class AguiaJWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	private static AguiaJWorkbenchWindowAdvisor instance;

	public static AguiaJWorkbenchWindowAdvisor getInstance() {
		return instance;
	}

	public AguiaJWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		instance = this;
	}


	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new AguiaJActionBarAdvisor(configurer);
	}


	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();		
		configurer.setShowCoolBar(false);
		configurer.setShowStatusLine(false);
		configurer.setShowPerspectiveBar (false);
		updateTitle();
	}

	public void updateTitle() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();		
		configurer.setTitle("AGUIA/J - " + AguiaJHelper.getWorkingDirectory());
	}

	/**
	 * Overriden to maximize the window when shwon.
	 */

	public void postWindowCreate() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		IWorkbenchWindow window = configurer.getWindow();
		window.getShell().setMaximized(true);
	}
}
