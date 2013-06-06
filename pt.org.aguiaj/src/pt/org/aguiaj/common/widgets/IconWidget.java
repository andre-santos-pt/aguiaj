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
package pt.org.aguiaj.common.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISizeProvider;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJImage;

public class IconWidget extends Composite {

	private static final int SIDE = 16;
	
	public IconWidget(Composite parent, Image image) {
		super(parent, SWT.NONE);
		setBackgroundImage(image);
	}
	
	public IconWidget(Composite parent, Class<?> clazz) {
		this(parent, ClassModel.getInstance().getIcon(clazz));
	}
	
	public static IconWidget createForRowLayout(Composite parent, Image image) {
		IconWidget widget = new IconWidget(parent, image);
		widget.setLayoutData(new RowData(SIDE, SIDE));
		return widget;
	}
	
	public static IconWidget createForRowLayout(Composite parent, Class<?> clazz) {
		IconWidget widget = new IconWidget(parent, clazz);
		widget.setLayoutData(new RowData(SIDE, SIDE));
		return widget;
	}



}
