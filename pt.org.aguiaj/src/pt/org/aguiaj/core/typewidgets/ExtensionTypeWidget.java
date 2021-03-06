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


import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.common.widgets.NullReferenceWidget;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.extensibility.VisualizationWidget;


// TODO: fail-proof
public class ExtensionTypeWidget extends AbstractTypeWidget {

	private VisualizationWidget extension;

	private Object object;
	private String text;
	
	private StackLayout stack;
	private NullReferenceWidget nullWidget;
	private Composite extensionWidget;

	public ExtensionTypeWidget(
			Composite parent,
			Class<?> type,
			WidgetProperty property, 
			VisualizationWidget extension) {

		super(parent, SWT.NONE, property, false);

		this.extension = extension;
		text = "null";
		extension.createSection(extensionWidget);
	}

	public void paintWidget(Color color) {
		nullWidget.setBackground(color);
		extensionWidget.setBackground(color);
	}
	
	private void updateNullWidget() {
		Point size = extensionWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		nullWidget.update(Math.min(size.x, size.y));
	}

	@Override
	protected void createContents(Composite parent) {
		stack = new StackLayout();
		setLayout(stack);
		setBackground(parent.getBackground());

		extensionWidget = new Composite(parent, SWT.NONE);
		
		nullWidget = new NullReferenceWidget(parent, SWT.NONE);
		updateNullWidget();

		stack.topControl = nullWidget;
	}

	public Object defaultValue() {
		return null;
	}


	public Control getControl() {
		return this;
	}


	public Object getObject() {
		return object;
	}

	@Override
	public String getTextualRepresentation() {		
		return text;
	}

	public void setToolTipText(String text) {

	}

	@Override
	public final void update(Object object) {
		this.object = object;
		text = ReflectionUtils.getTextualRepresentation(object, true);
		
		if(isDisposed())
			return; 

		if(object == null) {
			if(stack.topControl != nullWidget) {
				updateNullWidget();
				stack.topControl = nullWidget;
				layout();
				getParent().layout();
				getParent().pack();
			}
		}
		else {
			extension.update(object);	
			if(extension.needsRelayout()) {
				extensionWidget.layout();
				extensionWidget.pack();
				updateNullWidget();
				getParent().pack();
				getParent().layout();
//				extensionWidget.getParent().layout();
//				extensionWidget.getParent().pack();
			}
			if(stack.topControl != extensionWidget) {
				stack.topControl = extensionWidget;
				layout();
				getParent().layout();
				getParent().pack();
			}
		}
	}
}
