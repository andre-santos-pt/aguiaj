/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.standard.extensions;

import java.lang.reflect.Array;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

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


	public void update(Object object) {
		if(row == null && object != null) {
			new ArrayLengthWidget(section).update(Array.getLength(object));
			Class<?> arrayType = object.getClass().getComponentType();
			row = new ArrayRowWidget(section, arrayType, object, fieldContainer);
			SWTUtils.setColorRecursively(section, section.getBackground());
		}

		// update is only about the fields (which are handled in ObjectWidget)
		if(object != null)
			row.updateFields(object);
	}
	
}
