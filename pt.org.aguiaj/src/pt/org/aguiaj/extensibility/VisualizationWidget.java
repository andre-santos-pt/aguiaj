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
package pt.org.aguiaj.extensibility;

import org.eclipse.swt.widgets.Composite;


/**
 * 
 * @author andre
 *
 * @param <T>
 */
public interface VisualizationWidget<T> extends CustomWidget<T> {
	/**
	 * Creates the visualization section contents. Should only be called by the framework.
	 * 
	 * @param parent Parent widget -- Contract: will never be passed null
	 */
	void createSection(Composite parent);
	
	/**
	 * Does the widget needs relayout?
	 * @return true if yes, false otherwise
	 */
	boolean needsRelayout();
	
	/**
	 * Obtains this widget's main control.
	 * 
	 * @return a non-null reference to a subtype of Control. Contract: should not return null in any case.
	 */
//	Control getControl();
	
//	void dispose();
	
	
	boolean include(Class<?> type);
	
	public abstract static class Adapter<T> implements VisualizationWidget<T> {
		@Override
		public void createSection(Composite parent) {
			
		}
		
		@Override
		public boolean needsRelayout() {
			return true;
		}
		
//		public void dispose() {
//			
//		}
		
		@Override
		public boolean include(Class<?> type) {
			return true;
		}
	}
}
