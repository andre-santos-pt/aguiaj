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
package pt.org.aguiaj.extensibility.canvas;

import java.util.List;

import org.eclipse.swt.widgets.Canvas;

import pt.org.aguiaj.extensibility.CustomWidget;


public interface CanvasVisualizationWidget<T> extends CustomWidget<T> {	
	// argument not null
	void initialize(Canvas canvas);
	
	// positive
	int canvasWidth();
	
	// positive
	int canvasHeight();
	
	// not null
	List<DrawItem> drawItems();
}
