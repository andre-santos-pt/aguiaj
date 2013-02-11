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
package pt.org.aguiaj.core.typewidgets;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.NullReferenceWidget;
import pt.org.aguiaj.extensibility.CanvasVisualizationWidget;

// TODO : safe execute
class CanvasObjectWidgetExtension extends AbstractTypeWidget implements PaintListener {


	private Composite parent;
	private Canvas canvas;
	private StackLayout stack;
	private NullReferenceWidget nullWidget;
	private Composite extensionWidget;

	private Object object;

	private CanvasVisualizationWidget extension;

	private RowLayout layout;

	public CanvasObjectWidgetExtension(Composite parent, CanvasVisualizationWidget<?> extension, WidgetProperty type) {
		super(parent, SWT.NONE, type, false);		
		this.extension = extension;
		update(getObject());
	}

	@Override
	protected void createContents(Composite parent) {
		this.parent = parent;
		stack = new StackLayout();		
		parent.setLayout(stack);
		extensionWidget = new Composite(parent, SWT.NONE);
		layout = new RowLayout();
		extensionWidget.setLayout(layout);
		canvas = new Canvas(extensionWidget, SWT.NONE);		
		canvas.addPaintListener(this);
		nullWidget = new NullReferenceWidget(parent, SWT.NONE);
		stack.topControl = nullWidget;
		addMenu();
	}

	public void initialize() {
		initialize(canvas);
	}

	public void initialize(Canvas canvas) {
		extension.initialize(canvas);
	}

	public final void createSection(Composite parent, Object object, String reference) {		
		update(object);

	}

	@Override
	public final void update(Object object) {
		this.object = object;

		if(isDisposed())
			return; 

		if(object == null) {
			if(stack.topControl != nullWidget) {
				nullWidget.update(Math.min(extension.canvasWidth(), extension.canvasHeight()));
				stack.topControl = nullWidget;
			}
		}
		else {
			extension.update(object);	
			List<Rectangle> redraw = extension.toRedraw();
			for(Rectangle r : redraw)		
				canvas.redraw(r.x, r.y, r.width, r.height, false);

			canvas.setLayoutData(new RowData(extension.canvasWidth(), extension.canvasHeight()));
			canvas.update();
			canvas.layout();
			canvas.getParent().pack();

			if(stack.topControl != extensionWidget) {
				stack.topControl = extensionWidget;
			}
		}

		parent.pack();
		parent.layout();
	}

//	@Override
//	public List<Rectangle> toRedraw() {
//		return extension.toRedraw();
//	}

	@Override
	public final void paintControl(PaintEvent e) {
		if(getObject() != null)
			extension.drawObject(e.gc);
	}

	@Override
	public Object defaultValue() {
		return null;
	}

	@Override
	public final Control getControl() {
		return canvas;
	}



	// TODO error-safe
//	@Override
//	public int canvasWidth() {
//		return extension.canvasWidth();
//	}
//
//	// TODO error-safe
//	@Override
//	public int canvasHeight() {
//		return extension.canvasHeight();
//	}
//
//	// TODO error-safe
//	@Override
//	public void drawObject(GC gc) {
//		extension.drawObject(gc);
//	}

	@Override
	public Object getObject() {		
		return object;
	}


	@Override
	public String getTextualRepresentation() {
		return object == null ? "null" : object.toString();
	}

	private void addMenu() {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Save to file");
		item.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				//				try {
				//					Thread.sleep(500);
				//				} catch (InterruptedException e1) {					
				//					e1.printStackTrace();
				//				}
				SWTUtils.saveImageToFile(canvas, getObject().getClass().getSimpleName() + ".png");
			}
		});
		canvas.setMenu(menu);
	}

}
