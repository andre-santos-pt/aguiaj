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
package pt.iscte.dcti.aguiaj.editor;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public enum TokenColor {
	BLACK(0, 0, 0),
	STRING(0, 0, 255),
	CHAR(0, 128, 128),	
	KEYWORD(128, 0, 128),
	NUMBER(0, 0, 255),
	COMMENT(128, 128, 128),
	ID(255, 0, 0),
	TRUE(0, 128, 0),
	FALSE(255, 0, 0),
	NULL(255, 0, 239);
	
	public final Color color;
	
	private TokenColor(int r, int g, int b) {
		color = new Color(Display.getDefault(), new RGB(r, g, b));
	}
}
