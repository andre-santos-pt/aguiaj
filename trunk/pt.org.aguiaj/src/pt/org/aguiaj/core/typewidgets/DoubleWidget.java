/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.core.typewidgets;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.PluggableWidget;

@PluggableWidget(double.class)
class DoubleWidget extends PrimitiveTypeWidget {

	private Text text;
	private static Double defaultValue = new Double(0.0);
//	private static DecimalFormat df = new DecimalFormat("0.00");
	
	public DoubleWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	private VerifyListener listener;
	
	private class InputListener extends VerifyListener {		
		public InputListener(Text text) {
			super(text);			
		}
		
		public boolean charOk(int code) {
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
	
	@Override
	protected void createContents(Composite parent) {
		text = new Text(parent, SWT.BORDER |
				(getUsageType() == WidgetProperty.PROPERTY || !isModifiable() ? SWT.READ_ONLY : SWT.NONE)); 
		
		if(getUsageType() == WidgetProperty.PARAMETER)
			text.setLayoutData(new RowData(35, 15));
		
		listener = new InputListener(text);
		text.addListener(SWT.Verify, listener);
		setVerifyListener(listener);
		
		if(getUsageType() != WidgetProperty.PARAMETER)
			addFocusListener(text);
		
		update(defaultValue);
		
//		if(isModifiable())
//			addEnterKeyListener(text);
//		
//		text.addListener(SWT.Verify, new Listener() {			
//			
//			public void handleEvent(Event e) {
//				switch(e.keyCode) {
//				case SWT.BS:
//				case SWT.DEL:
//				case SWT.HOME:
//				case SWT.END:
//				case SWT.ARROW_LEFT:
//				case SWT.ARROW_RIGHT:
//					return;
//				default:
//					if(!(
//						e.character == 0 ||
//						Character.isDigit(e.character) && text.getText().isEmpty() || 
//						Character.isDigit(e.character) && text.getCaretPosition() == 0 && text.getText().charAt(0) != '-' ||
//						Character.isDigit(e.character) && text.getCaretPosition() > 0 ||
//						e.character == '-' && text.getCaretPosition() == 0 && text.getText().indexOf('-') == -1 ||
//						e.character == '.' && text.getText().isEmpty() ||
//						e.character == '.' && text.getText().indexOf('.') == -1 && text.getCaretPosition() == 0 && text.getText().charAt(0) != '-' ||
//						e.character == '.' && text.getText().indexOf('.') == -1 && text.getCaretPosition() > 0
//					))
//							e.doit = false;
//				}
//			}
//		});
	}
	
	public Double getObject() {	
		if(text == null || text.isDisposed())
			return defaultValue;
		
		try {
			return new BigDecimal(text.getText()).doubleValue(); //.replaceAll(",", "."));
		}
		catch(NumberFormatException e) {
			return defaultValue;
		}
	}

	
	public void update(Object object) {
		if(!text.isDisposed() && object != null) {
			listener.setIgnore();
			text.setText(object.toString());
			listener.unsetIgnore();
			layout();
			getParent().layout();
			getParent().pack();
		}
	}

	
	public Double defaultValue() {
		return defaultValue;
	}

	
	public String toString() {
		return getObject().toString();
	}

	
	public Control getControl() {
		return text;
	}
}
