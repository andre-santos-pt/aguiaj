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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.PluggableWidget;


@PluggableWidget({int.class, long.class})
class IntegerWidget extends TextTypeWidget {

	private static final Integer defaultValue = new Integer(0);
	
	private class InputListener extends VerifyListener {		
		public boolean charOk(int code) {
			Text text = getText();
			switch(code) {
			case SWT.BS:
			case SWT.DEL:
			case SWT.HOME:
			case SWT.END:
			case SWT.ARROW_LEFT:
			case SWT.ARROW_RIGHT:
				return true;
			default:
				if(!(
					code == 0 ||
					Character.isDigit(code) && text.getText().isEmpty() || 
					Character.isDigit(code) && text.getCaretPosition() == 0 && text.getText().charAt(0) != '-' ||
					Character.isDigit(code) && text.getCaretPosition() > 0 ||
					code == '-' && text.getCaretPosition() == 0 && text.getText().indexOf('-') == -1))
						return false;
			}			
			return Character.isDigit(code) || code == '-';
		}
	}
	
	public IntegerWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	public Integer getObject() {
		Text text = getText();
		if(text == null || text.isDisposed())
			return defaultValue;
		
		try {
			return new Integer(text.getText());
		}
		catch(NumberFormatException e) {
			return defaultValue;
		}
	}

	public Integer defaultValue() {
		return defaultValue;
	}

	@Override
	protected VerifyListener createVerifyListener() {
		return new InputListener();
	}

	@Override
	protected int getWidth() {
		return 35;
	}
}
