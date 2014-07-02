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
package pt.org.aguiaj.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import pt.org.aguiaj.extensibility.AguiaJHelper;

public class AguiaJConsoleActivator implements BundleActivator {
	private static AguiaJConsoleActivator instance;
	private static BundleContext context;
	
	private MessageConsole console;
	private MessageConsoleStream out;
	
	static BundleContext getContext() {
		return context;
	}
	
	public static AguiaJConsoleActivator getInstance() {
		return instance;
	}
	
	public void start(BundleContext bundleContext) throws Exception {
		instance = this;
		AguiaJConsoleActivator.context = bundleContext;
		console = findConsole("AguiaJ");
		out = console.newMessageStream();
	}

	public void stop(BundleContext bundleContext) throws Exception {
		AguiaJConsoleActivator.context = null;
	}
	
	private static MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		MessageConsole myConsole = new MessageConsole(name, null);
		myConsole.setFont(new Font(null, "Courier", 14, SWT.NONE));
		conMan.addConsoles(new IConsole[]{myConsole});
		return myConsole;
	}
	
	public void writeToConsole(Object obj) {	
		console.activate();
		out.print(AguiaJHelper.getTextualRepresentation(obj));
		out.println();
	}
	
	
	public void clearConsole() {
		console.clearConsole();
	}

	

}
