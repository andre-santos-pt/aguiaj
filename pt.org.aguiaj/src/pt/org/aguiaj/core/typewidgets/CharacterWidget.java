/*******************************************************************************
 * Copyright (c) 2012 AndrŽ L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     AndrŽ L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.typewidgets;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.PluggableWidget;
import pt.org.aguiaj.core.AguiaJParam;

@PluggableWidget(char.class)
class CharacterWidget extends PrimitiveTypeWidget {

	private Text text;
	private static Character defaultValue = new Character(' ');
	
	public CharacterWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	private class InputListener extends VerifyListener {		
		public InputListener(Text text) {
			super(text);			
		}
		
		public boolean charOk(int code) {
			return
				code == SWT.BS ||
				isValidChar(code) && text.getText().length() == 0;
		}
		
		private boolean isValidChar(int code) {
			return 
			Character.isLetterOrDigit(code) ||
			code == ' ';
		}
	}
	
	
	private VerifyListener listener;

	@Override
	protected void createContents(Composite parent) {
		text = new Text(parent, SWT.BORDER |
				(getUsageType() == WidgetProperty.PROPERTY || !isModifiable() ? SWT.READ_ONLY : SWT.NONE));

		FontData data = new FontData("Courier", AguiaJParam.MEDIUM_FONT.getInt(), SWT.NONE);
		Font font = new Font(Display.getDefault(), data);
		text.setFont(font);
		
		text.setLayoutData(new RowData(15, data.getHeight() + 2));
		
		listener = new InputListener(text);
		text.addListener(SWT.Verify, listener);
		setVerifyListener(listener);
		
		if(getUsageType() != WidgetProperty.PARAMETER)
			addFocusListener(text);
		
		update(defaultValue);
	}

	public String getTextualRepresentation() {
		return "'" + getObject().toString() + "'";
	}

	@Override
	public Object getObject() {
		if(text == null || text.isDisposed())
			return defaultValue;

		String s = text.getText();
		return s.length() > 0 ? new Character(s.charAt(0)) : defaultValue();
	}


	public void update(Object object) {
		if(!text.isDisposed() && object != null) {			
			listener.setIgnore();
			text.setText(object.toString());			
			listener.unsetIgnore();
			layout();
			getParent().layout();
		}
	}

	public Character defaultValue() {
		return defaultValue;
	}


	public String toString() {
		return "'" + (text != null ? text.getText() : "") + "'";
	}


	public Control getControl() {
		return text;
	}
}
