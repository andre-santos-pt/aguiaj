/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.common;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public enum AguiaJColor {
	OBJECT_AREA(255, 255, 255),
	OBJECT(237, 237, 237),
//	DEAD_OBJECT(140, 140, 140),
	HIGHLIGHT(200, 200, 200),
	VALUECHANGE(0, 160, 240),
	ALERT(208, 44, 44),
	NULL(208, 130, 130),
	PRIVATES(140, 140, 140),
	WHITE(255, 255, 255),
	BLACK(0, 0, 0),
	GRAY(120, 120, 120);
	
	
	private Color color;	
	
	private AguiaJColor(int r, int g, int b) {		
		color = new Color(Display.getDefault(), new RGB(r, g, b));
	}
	
	public Color getColor() {
		return color;
	}
}
