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
package pt.iscte.dcti.aguiaj;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class SlidesView extends ViewPart {

	public static final String ID = "pt.iscte.dcti.aguiaj.slides";

	@Override
	public void createPartControl(Composite parent) {
		Browser browser = new Browser(parent, SWT.NONE);
		browser.setUrl("https://docs.google.com/present/view?id=dfbm24wb_230g6fz99hk");
	}

	@Override
	public void setFocus() {

	}

}
