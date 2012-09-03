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
package pt.org.aguiaj.objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.AguiaJImage;

public class NullWidget extends Composite {

	public NullWidget(Composite parent) {
		super(parent, SWT.BORDER);
		setLayout(new FillLayout());
		setLayoutData(new RowData(110, 200));		
		setBackgroundImage(AguiaJImage.NULL.getImage());					
		setToolTipText("null");		
	}
	
	public void show() {
		setLayoutData(new RowData(110, 200));
		setVisible(true);
	}
	
	public void hide() {
		setLayoutData(new RowData(0, 0));
		setVisible(false);
	}

}
