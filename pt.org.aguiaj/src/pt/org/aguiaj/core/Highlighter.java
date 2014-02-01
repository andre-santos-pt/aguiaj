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
package pt.org.aguiaj.core;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

public class Highlighter implements Highlightable {
	private static final int FACTOR = 50;
	
	
	private Control control;
	private Color prevColor;

	public Highlighter(Control control) {
		this.control = control;
	}

	@Override
	public void highlight() {
		if(!control.isDisposed()) {
			if(prevColor == null)
				prevColor = control.getBackground();
			
			control.setBackground(darken(control.getBackground()));
		}
	}
	
	private static Color darken(Color c) {
		int r = Math.max(0, c.getRed() - FACTOR);
		int g = Math.max(0,c.getGreen() - FACTOR);
		int b =  Math.max(0,c.getBlue() - FACTOR);
		return new Color(c.getDevice(), r,g,b);
	}

	@Override
	public void unhighlight() {
		if(!control.isDisposed()) {
			if(prevColor != null) {
				control.setBackground(prevColor);
				prevColor = null;
			}
		}
	}

}
