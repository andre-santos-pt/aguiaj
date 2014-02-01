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
package pt.org.aguiaj.core.typewidgets;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.NullReferenceWidget;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.extensibility.canvas.CanvasVisualizationWidget;
import pt.org.aguiaj.extensibility.canvas.DrawItem;

// TODO : safe execute -- missing initialize
class CanvasObjectWidgetExtension extends AbstractTypeWidget implements PaintListener {


	private Composite parent;
	private Canvas canvas;
	private StackLayout stack;
	private NullReferenceWidget nullWidget;
	private Composite extensionWidget;

	private Object object;

	private final CanvasVisualizationWidget<?> extension;

	private MethodInvocationCommand canvasWidthCommand;
	private MethodInvocationCommand canvasHeightCommand;
	private MethodInvocationCommand drawItemsCommand;
//	private MethodInvocationCommand initializeCommand;

	private RowLayout layout;

	public CanvasObjectWidgetExtension(Composite parent, CanvasVisualizationWidget<?> extension, WidgetProperty type) {
		super(parent, SWT.NONE, type, false);	
		assert extension != null;
		this.extension = extension;
		canvasWidthCommand = MethodInvocationCommand.instanceInvocation(extension, "canvasWidth");
		canvasHeightCommand = MethodInvocationCommand.instanceInvocation(extension, "canvasHeight");
		drawItemsCommand = MethodInvocationCommand.instanceInvocation(extension, "drawItems");
//		initializeCommand = MethodInvocationCommand.instanceInvocation(extension, "initialize", new Class[] {Canvas.class}, new Object[] {canvas});
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
				nullWidget.update(Math.min(canvasWidth(), canvasHeight()));
				stack.topControl = nullWidget;
			}
		}
		else {
			if(updateObject(object)) {
				canvas.redraw();
				canvas.setLayoutData(new RowData(canvasWidth(), canvasHeight()));
				canvas.update();
				canvas.layout();
				canvas.getParent().pack();

				if(stack.topControl != extensionWidget) {
					stack.topControl = extensionWidget;
				}
			}
			else {
				stack.topControl = nullWidget;
			}
		}

		parent.pack();
		parent.layout();
	}


	@Override
	public final void paintControl(PaintEvent e) {
		if(ok) {
			e.gc.setBackground(parent.getBackground());
			e.gc.fillRectangle(e.gc.getClipping());
			if(getObject() != null) {
				List<DrawItem> items = drawItems();
				for(DrawItem item : items) {
					item.draw(e.gc);
				}
			}
		}
		else {
			e.gc.setBackground(new Color(null, 0,0,0));
			e.gc.fillRectangle(e.gc.getClipping());
		}
	}


	@Override
	public Object defaultValue() {
		return null;
	}

	@Override
	public final Control getControl() {
		return canvas;
	}


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
				SWTUtils.saveImageToFile(canvas, getObject().getClass().getSimpleName() + ".png");
			}
		});
		canvas.setMenu(menu);
	}


	//	private void initialize(Canvas canvas) {
	//		Method method = null;
	//		try {
	//			method = extension.getClass().getMethod("initialize", Canvas.class);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		MethodInvocationCommand initializeCommand = new MethodInvocationCommand(extension, null, method, new Object[] { canvas },  null);
	//		ExceptionHandler.INSTANCE.execute(initializeCommand);
	//	}

//	private void initialize(Canvas canvas) {
//		if(initializeCommand != null) {
//			Display.getDefault().syncExec(new Runnable() {
//				
//				@Override
//				public void run() {
//					if(!ExceptionHandler.INSTANCE.execute(initializeCommand, object))
//						stack.topControl = nullWidget;
//				}
//			});
//		}
//	}

	private int canvasWidth() {
		return getValueDim(canvasWidthCommand);
	}

	private int canvasHeight() {
		return getValueDim(canvasHeightCommand);
	}
	
	private int getValueDim(MethodInvocationCommand cmd) {
		if(cmd != null && ExceptionHandler.INSTANCE.execute(cmd, object)) {	
			return (Integer) cmd.getResultingObject();
		}
		else {
			stack.topControl = nullWidget;
			return 1;
		}
	}

	
	
	

	private boolean ok = true;
	private List<DrawItem> drawItems() {
		ok = false;
		ok = ExceptionHandler.INSTANCE.execute(drawItemsCommand, object);
		if(ok) {	
			stack.topControl = extensionWidget;
			return (List<DrawItem>) drawItemsCommand.getResultingObject();
		}
		else {
			stack.topControl = nullWidget;
			return Collections.emptyList();
		}
	}

	private boolean updateObject(Object object) {
		Method method = null;
		try {
			method = extension.getClass().getMethod("update", Object.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MethodInvocationCommand updateCommand = new MethodInvocationCommand(extension, null, method, new Object[] { object },  null);
		return ExceptionHandler.INSTANCE.execute(updateCommand, object);
	}

}
