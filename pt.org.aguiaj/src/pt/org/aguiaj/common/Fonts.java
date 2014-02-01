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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.core.AguiaJParam;

public class Fonts {
	private static Fonts instance;
	
	private Map<FontData, Font> fonts;
	
	private Fonts() {
		fonts = new HashMap<FontData, Font>();
	}
	
	public static Fonts getInstance() {
		if(instance == null)
			instance = new Fonts();
		
		return instance;
	}
	
	
	
	
	public static void set(Control control, AguiaJParam fontSize, int ... styles) {
		getInstance().setFont(control, AguiaJParam.FONT.getString(), fontSize.getInt(), styles);
	}
	
	public static void set(Control control, int size, int ... styles) {
		getInstance().setFont(control, AguiaJParam.FONT.getString(), size, styles);
	}
	
	public static void set(Control control, AguiaJParam face, AguiaJParam fontSize, int ... styles) {
		getInstance().setFont(control, face.getString(), fontSize.getInt(), styles);
	}
	
	public static void set(Control control, String face, AguiaJParam fontSize, int ... styles) {
		getInstance().setFont(control, face, fontSize.getInt(), styles);
	}
	
	public static void set(Control control, String face, int size, int ... styles) {
		getInstance().setFont(control, face, size, styles);
	}
	
	private void setFont(Control control, String face, int size, int ... styles) {
		FontData data = createFontData(face, size, styles);
			
		Font font = fonts.containsKey(data) ? fonts.get(data) : new Font(Display.getDefault(), data);
		if(!fonts.containsKey(data))
			fonts.put(data, font);
		
		control.setFont(font);
	}

	private static FontData createFontData(String face, int size, int... styles) {
		int style = SWT.NONE;
		for(int s : styles)
			style |= s;
		
		FontData data = new FontData(face, size, style);
		return data;
	}
}
