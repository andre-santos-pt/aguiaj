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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;



abstract class PrimitiveTypeWidget extends AbstractTypeWidget {
	private VerifyListener verifyListener;
	private boolean dirty;
	private String last;
	
	public abstract class VerifyListener implements Listener {
		private boolean ignore;
		private Text text;
		
		public VerifyListener(Text text) {
			this.text = text;
		}
		
		public boolean ignore() {
			return ignore;
		}
		
		public void setIgnore() {
			ignore = true;
		}
		
		public void unsetIgnore() {
			ignore = false;
		}
		
		@Override
		public void handleEvent(Event event) {
			if(ignore())
				return;
			
			if(charOk(event.character)) {
				if(!dirty) {
					last = text.getText();
					dirty = true;
				}
			}
			else
				event.doit = false;
		}
		
		public abstract boolean charOk(int code);
	}
	
	public PrimitiveTypeWidget(Composite parent, WidgetProperty type, boolean modifiable) {
		super(parent, SWT.NONE, type, modifiable);		
	}
	
	public String getTextualRepresentation() {
		return getObject().toString();
	}
	
	protected void setVerifyListener(VerifyListener verifyListener) {
		this.verifyListener = verifyListener;
		verifyListener.text.addKeyListener(new KeyAdapter() {			
			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.CR) {
					dirty = false;
				}
			}
		});
	}
	
	protected void addFocusListener(final Text text) {
		if(isModifiable())
			addEnterKeyListener(text);
		
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if(dirty) {
					verifyListener.setIgnore();
					text.setText(last);
					verifyListener.unsetIgnore();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
			}
		});
	}
	
}
