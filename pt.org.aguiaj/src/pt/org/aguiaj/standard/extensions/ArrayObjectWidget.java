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

import java.lang.reflect.Array;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.extensibility.VisualizationWidget;

@PluggableObjectWidget({
	int[].class, 
	double[].class, 
	char[].class, 
	boolean[].class, 
	Object[].class
})
public class ArrayObjectWidget extends VisualizationWidget.Adapter<Object> {
	private ArrayLengthWidget length;
	private ArrayRowWidget row;
	private FieldContainer fieldContainer;
	private Composite section;

	@Override
	public void createSection(Composite section) {
		this.fieldContainer = findFieldContainer(section);
		section.setLayout(new RowLayout(SWT.VERTICAL));
		this.section = section;
	}

	private FieldContainer findFieldContainer(Composite c) {
		if(c instanceof FieldContainer)
			return (FieldContainer) c;
		else
			return findFieldContainer(c.getParent());
	}

	private void clear() {
		if(length != null) {
			length.dispose();
			row.dispose();
		}
	}

	public void update(Object object) {
		clear();
		length = new ArrayLengthWidget(section);
		length.update(Array.getLength(object));
		Class<?> arrayType = object.getClass().getComponentType();
		row = new ArrayRowWidget(section, arrayType, object, fieldContainer);
		row.updateFields(object);
		SWTUtils.setColorRecursively(section, section.getBackground());
	}

}
