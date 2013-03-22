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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.PluggableWidget;
import pt.org.aguiaj.core.AguiaJParam;


@PluggableWidget(int.class)
class IntegerWidget extends PrimitiveTypeWidget {

	private Text text;
	private static final Integer defaultValue = new Integer(0);
	
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
					code == '-' && text.getCaretPosition() == 0 && text.getText().indexOf('-') == -1))
						return false;
			}			
			return Character.isDigit(code) || code == '-';
		}
	}
	
	public IntegerWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	private VerifyListener listener;
	
	@Override
	protected void createContents(Composite parent) {
		text = new Text(parent, SWT.BORDER |
				(getUsageType() == WidgetProperty.PROPERTY || !isModifiable() ? SWT.READ_ONLY : SWT.NONE));
		
		FontData data = new FontData("Courier", AguiaJParam.MEDIUM_FONT.getInt(), SWT.NONE);
		Font font = new Font(Display.getDefault(), data);
		text.setFont(font);
		
		if(getUsageType() == WidgetProperty.PARAMETER) {
			text.setLayoutData(new RowData());
			((RowData) text.getLayoutData()).width = 35;
		}
			
		listener = new InputListener(text);
		text.addListener(SWT.Verify, listener);
		setVerifyListener(listener);
		
		if(getUsageType() != WidgetProperty.PARAMETER)
			addFocusListener(text);
		
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				text.selectAll();	
			}
		});
		
		update(defaultValue);
	}
	
	
	public Integer getObject() {
		if(text == null || text.isDisposed())
			return defaultValue;
		
		try {
			Integer i = new Integer(text.getText());
			return i;
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


	
	public Integer defaultValue() {
		return defaultValue;
	}

	
	public String toString() {
		return getObject().toString();
	}

	
	public Control getControl() {
		return text;
	}
}
