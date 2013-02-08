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

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJImage;

public class IconWidget extends Composite {

	private static final int SIDE = 16;
	
	public IconWidget(Composite parent, AguiaJImage image) {
		super(parent, SWT.NONE);
		setLayoutData(new RowData(SIDE, SIDE));
		setBackgroundImage(image.getImage());
	}
	
	public IconWidget(Composite parent, Class<?> clazz) {
		super(parent, SWT.NONE);
		setLayoutData(new RowData(SIDE, SIDE));
		Image icon = ClassModel.getInstance().getIcon(clazz);
		setBackgroundImage(icon);
	}


}
