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
package aguiaj.colors.view;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.extensibility.VisualizationWidget;
import aguiaj.colors.Color;

public class ColorWidget extends VisualizationWidget.Adapter<Color> {
	private Composite square;
	private Color color;
	private boolean relayout;
	
	@Override
	public void createSection(Composite section) {
		RowLayout layout = new RowLayout();
		layout.marginLeft = 15;
		section.setLayout(layout);
		square = new Composite(section, SWT.BORDER);
		square.setLayoutData(new RowData(32, 32));
		relayout = true;
	}

	@Override
	public void update(Color color) {
		if(!color.equals(this.color)) {
			this.color = color;
			square.setBackground(new org.eclipse.swt.graphics.Color(
					Display.getDefault(), color.getR(), color.getG(), color.getB()));
			relayout = true;
		}
		else {
			relayout = false;
		}
	}

	@Override
	public boolean needsRelayout() {
		return relayout;
	}

}
