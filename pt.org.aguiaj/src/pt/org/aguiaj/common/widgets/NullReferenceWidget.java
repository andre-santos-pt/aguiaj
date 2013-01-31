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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.AguiaJColor;

public class NullReferenceWidget extends Composite {
	private static final int MIN_SIDE = 24;
	
	private Canvas canvas;
	private int side;
	
	public NullReferenceWidget(final Composite parent, int style) {
		super(parent, style);
		this.side = MIN_SIDE;
		
		setLayout(new RowLayout());
		canvas = new Canvas(this, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				int rad = (int) (side*((double) 5/7));
				int margin =  (int) (side*((double) 1/7));
				e.gc.setBackground(parent.getBackground());
				e.gc.fillRectangle(e.gc.getClipping());
				e.gc.setForeground(AguiaJColor.BLACK.getColor());
				e.gc.setLineWidth(1);
				e.gc.drawOval(margin, margin, rad, rad); 
				e.gc.drawLine(margin, side-margin, side-margin, margin);
			}
		});
		canvas.setLayoutData(new RowData(side, side));	
	}
	
	public void update(int side) {
		this.side = side < MIN_SIDE ? MIN_SIDE : side;
		
		canvas.setLayoutData(new RowData(this.side, this.side));			
		canvas.update();
		canvas.redraw();
		canvas.layout();
		pack();
		layout();
	}
}
