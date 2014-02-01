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
package pt.org.aguiaj.standard.extensions;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.extensibility.VisualizationWidget;


public class ObjectToStringWidget extends VisualizationWidget.Adapter<Object> {

	private VisualizationWidget<Object> widget;

	@Override
	public void update(Object object) {
		widget.update(object.toString());
	}

	@Override
	public void createSection(Composite parent) {
		widget = new StringObjectWidget();
		widget.createSection(parent);
	}

	@Override
	public boolean needsRelayout() {
		return widget.needsRelayout();
	}
	
	@Override
	public boolean include(Class<?> type) {
		return ReflectionUtils.declaresToString(type);
	}
}
