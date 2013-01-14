/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.eclipse;

import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.ExceptionListener;
import pt.org.aguiaj.extensibility.ExceptionTrace;
import pt.org.aguiaj.extensibility.TraceLocation;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private static IJavaProject project;
	private static String perspective;
	
	private static Activator instance;
	
	private ExceptionTrace trace;
	
	public Activator() {
		instance = this;
	}
	
	public static Activator getInstance() {
		return instance;
	}
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		AguiaJHelper.addExceptionListener(new ExceptionListener() {
			@Override
			public void newException(ExceptionTrace t) {
				trace = t;
				TraceBack cmd = new TraceBack();
				cmd.run(null);
			}
		});
	}
	
	public TraceLocation getLocation() {
		return trace.getLocation();
	}
	
	public void moveNextLocation() {
		trace.moveFrontwards();
	}
	
	public void movePreviousLocation() {
		trace.moveBackwards();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	static void setProject(IJavaProject proj) {
		project = proj;
	}

	static IJavaProject getProject() {
		return project;
	}

	static void setPerspective(String id) {
		perspective = id;
	}
	
	static String getPerspective() {
		return perspective;
	}
}
