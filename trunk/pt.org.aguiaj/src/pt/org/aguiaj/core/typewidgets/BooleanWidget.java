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
package pt.org.aguiaj.core.typewidgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.common.PluggableWidget;



@PluggableWidget(boolean.class)
class BooleanWidget extends PrimitiveTypeWidget {

	private Button checkbox;
	
	public BooleanWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	protected void createContents(Composite parent) {
		checkbox = new Button(parent, SWT.CHECK);
		checkbox.setEnabled(getUsageType() != WidgetProperty.PROPERTY && isModifiable());
		checkbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				switch(getUsageType()) {
				case ATTRIBUTE: modifyAttribute(); break;
				case ARRAYPOSITION: modifyArrayPosition(null); break;
				}			
			}
		});
	}
	
	public Boolean getObject() {
		if(!checkbox.isDisposed())
			return checkbox.getSelection();
		return null;
	}

	
	public void update(Object object) {
		if(!checkbox.isDisposed() && object != null) {
			checkbox.setSelection((Boolean) object);
			checkbox.setToolTipText(getObject().toString());
		}
	}

	
	public Boolean defaultValue() {
		return new Boolean(false);
	}

	
	public String toString() {
		if(checkbox != null)
			return new Boolean(checkbox.getSelection()).toString();
		else
			return defaultValue().toString();
	}
	
	public Control getControl() {
		return checkbox;
	}
}
