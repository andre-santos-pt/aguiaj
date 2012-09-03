/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.typewidgets;

import pt.org.aguiaj.common.PluggableWidget;
import pt.org.aguiaj.core.TypeWidget;

public enum ActiveTypeWidget {
	INT(IntegerWidget.class),
	DOUBLE(DoubleWidget.class),
	BOOLEAN(BooleanWidget.class),
	CHAR(CharacterWidget.class);
	
	private Class<? extends TypeWidget> clazz;
	
	private ActiveTypeWidget(Class<? extends TypeWidget> clazz) {
		this.clazz = clazz;
	}
	
	public static void loadTypeWidgets() {
		for(ActiveTypeWidget w : values()) {
			PluggableWidget ann = w.clazz.getAnnotation(PluggableWidget.class);
			WidgetFactory.INSTANCE.addWidgetType(ann.value(), w.clazz);
		}
	}
	
	
}
