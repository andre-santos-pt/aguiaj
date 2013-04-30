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

@PluggableWidget(char.class)
class CharacterWidget extends TextTypeWidget {

	private static Character defaultValue = new Character(' ');

	public CharacterWidget(Composite parent, final WidgetProperty type, boolean modifiable) {
		super(parent, type, modifiable);
	}

	private class InputListener extends VerifyListener {		

		public boolean charOk(int code) {
			Text text = getText();
			return
					code == SWT.BS ||
					isValidChar(code) && text.getText().length() == 0 ||
					text.getSelectionCount() == 1;
		}

		private boolean isValidChar(int code) {
			return 
					Character.isLetterOrDigit(code) ||
					code == ' ';
		}
	}

	public String getTextualRepresentation() {
		return "'" + getObject().toString() + "'";
	}

	@Override
	public Object getObject() {
		Text text = getText();
		if(text == null || text.isDisposed())
			return defaultValue;

		String s = text.getText();
		return s.length() > 0 ? new Character(s.charAt(0)) : defaultValue();
	}


	
	public Character defaultValue() {
		return defaultValue;
	}


	public String toString() {
		Text text = getText();
		return "'" + (text != null ? text.getText() : "") + "'";
	}

	@Override
	protected VerifyListener createVerifyListener() {
		return new InputListener();
	}

	@Override
	protected int getWidth() {
		return 15;
	}


}
