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
package pt.org.aguiaj.core.typewidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import pt.org.aguiaj.common.AguiaJColor;


class NotSupportedWidget extends AbstractTypeWidget {

	private Label label;
	private Class<?> clazz;
	
	public NotSupportedWidget(Composite parent, WidgetProperty type, Class<?> clazz) {
		super(parent, SWT.NONE, type, false);
		setLayout(new FillLayout());
		this.clazz = clazz;
	}

	
	@Override
	protected void createContents(Composite parent) {
		label = new Label(parent, SWT.BOLD);
		label.setText("N/A");
		label.setForeground(AguiaJColor.ALERT.getColor());
		if(clazz != null)
			label.setToolTipText("Support for " + clazz.getName() + " not available.");
	}
	
	public void update(Object object) {
	
	}
	
	public Object getObject() {
		return null;
	}
	
	public String getTextualRepresentation() {
		return null;
	}
	
	
	public Object defaultValue() {
		return null;
	}

	
	public Control getControl() {
		return label;
	}
}
