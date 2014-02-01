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
package pt.org.aguiaj.core.typewidgets;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.PluggableWidget;

@PluggableWidget(double.class)
class DoubleWidget extends TextTypeWidget {

	private static Double defaultValue = new Double(0.0);
	
	public DoubleWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

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
					code == '-' && text.getCaretPosition() == 0 && text.getText().indexOf('-') == -1 ||
					code == '.' && text.getText().isEmpty() ||
					code == '.' && text.getText().indexOf('.') == -1 && text.getCaretPosition() == 0 && text.getText().charAt(0) != '-' ||
					code == '.' && text.getText().indexOf('.') == -1 && text.getCaretPosition() > 0
				))
						return false;
			}
			return Character.isDigit(code) || code == '-' || code == '.';
		}
	}
	
	public Double getObject() {	
		Text text = getText();
		if(text == null || text.isDisposed())
			return defaultValue;
		
		try {
			return new BigDecimal(text.getText()).doubleValue(); //.replaceAll(",", "."));
		}
		catch(NumberFormatException e) {
			return defaultValue;
		}
	}

	public Double defaultValue() {
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
