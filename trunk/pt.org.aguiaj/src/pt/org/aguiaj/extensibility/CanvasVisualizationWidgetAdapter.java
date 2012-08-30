/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.extensibility;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

/**
 * Helper class with default implementations of {@link CanvasVisualizationWidget}
 *
 * @param <T> The type of domain object that this widget renders.
 */
public abstract class CanvasVisualizationWidgetAdapter<T> 
implements CanvasVisualizationWidget<T> {
	
	private final ArrayList<Rectangle> area;
	
	public CanvasVisualizationWidgetAdapter() {
		area = new ArrayList<Rectangle>(1);
		area.add(new Rectangle(0, 0, 0, 0));
	}
	
	@Override
	public void initialize(Canvas canvas) {		
		
	}

	@Override
	public List<Rectangle> toRedraw() {
		Rectangle r = area.get(0);
		r.width = canvasWidth();
		r.height = canvasHeight();
		return area;
	}
}
