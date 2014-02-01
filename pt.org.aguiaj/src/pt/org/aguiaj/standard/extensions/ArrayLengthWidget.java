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

import java.util.EnumSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;

class ArrayLengthWidget extends Composite {

	private TypeWidget lengthField;
	
	public ArrayLengthWidget(Composite parent) {
		super(parent, SWT.NONE);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		new LabelWidget.Builder()
		.medium()
		.text("length")
		.create(this);
		
		lengthField = WidgetFactory.INSTANCE.createWidget(
				this, 
				int.class, 
				EnumSet.of(WidgetProperty.ATTRIBUTE, WidgetProperty.NO_EXTENSION));
	}
	
	public void update(int length) {
		lengthField.update(length);
	}

}
