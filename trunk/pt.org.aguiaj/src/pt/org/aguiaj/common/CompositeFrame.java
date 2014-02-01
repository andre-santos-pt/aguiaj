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
package pt.org.aguiaj.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import pt.org.aguiaj.common.widgets.LabelWidget;

public class CompositeFrame {
	private static final int PADDING = 0;
	private static final GridLayout layout = createGridLayout();
	
	public static Composite create(Composite parent, final String title) {
		Composite comp = new Composite(parent, title != null ? SWT.BORDER : SWT.NONE);
		
		comp.setLayout(layout);
		if(title != null) {
			LabelWidget label = new LabelWidget.Builder().small().bold().text(title + ":").create(comp);
			new Label(comp, SWT.NONE);
		}
		return comp;
	}

	private static GridLayout createGridLayout() {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = PADDING;
		layout.verticalSpacing = PADDING;
		layout.marginBottom = PADDING;
		layout.marginTop = PADDING;
		layout.marginLeft = PADDING;
		layout.marginRight = PADDING;
		return layout;
	}

}
