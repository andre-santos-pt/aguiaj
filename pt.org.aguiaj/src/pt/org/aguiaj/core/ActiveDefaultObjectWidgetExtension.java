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
package pt.org.aguiaj.core;

import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import pt.org.aguiaj.standard.extensions.Array2DObjectWidget;
import pt.org.aguiaj.standard.extensions.ArrayObjectWidget;
import pt.org.aguiaj.standard.extensions.PluggableObjectWidget;
import pt.org.aguiaj.standard.extensions.StringObjectWidget;

public enum ActiveDefaultObjectWidgetExtension {	
	ARRAYOBJECT(ArrayObjectWidget.class),
	ARRAY2DOBJECT(Array2DObjectWidget.class),
	STRING(StringObjectWidget.class);
//	ARRAYOBJECTTABLE(TableArrayObjectWidget.class);
	
	private Class<? extends VisualizationWidget<?>> clazz;
	
	private ActiveDefaultObjectWidgetExtension(Class<? extends VisualizationWidget<?>> clazz) {
		this.clazz = clazz;
	}
	
	public static void loadExtensions() {
		for(ActiveDefaultObjectWidgetExtension e : values()) {
			PluggableObjectWidget ann = e.clazz.getAnnotation(PluggableObjectWidget.class);
			try {
				WidgetFactory.INSTANCE.addVisualizationWidgetType(ann.value(), e.clazz);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}
