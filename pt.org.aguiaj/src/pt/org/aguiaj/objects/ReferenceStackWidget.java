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
package pt.org.aguiaj.objects;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.widgets.NullReferenceWidget;

public class ReferenceStackWidget<C extends Composite> extends Composite {

	private C widget;
	private Composite referenceStack;
	private List<ReferenceWidget> referenceWidgets;

	private ReferenceStackWidget(Composite parent) {
		super(parent, SWT.NONE);
		assert parent != null;
		
		referenceWidgets = new ArrayList<ReferenceWidget>();

		setLayout(new RowLayout(SWT.HORIZONTAL));
		setBackground(AguiaJColor.OBJECT_AREA.getColor());
		referenceStack = new Composite(this, SWT.NONE);
		GridLayout layout = new GridLayout(1, true);
		layout.verticalSpacing = 10;
		referenceStack.setLayout(layout);
		referenceStack.setBackground(AguiaJColor.OBJECT_AREA.getColor());
	}
	
	public C getWidget() {
		return widget;
	}

	public static ReferenceStackWidget<ObjectWidget> newObject(Composite parent, Object object) {
		ReferenceStackWidget<ObjectWidget> w = new ReferenceStackWidget<ObjectWidget>(parent);
		w.widget = new ObjectWidget(w, object);
		return w;
	}
	
	public static ReferenceStackWidget<ObjectWidget> newDeadObject(Composite parent, Object object) {
		ReferenceStackWidget<ObjectWidget> w = newObject(parent, object);
		w.widget.die();
		return w;
	}
	
	public static ReferenceStackWidget<NullReferenceWidget> newNull(Composite parent) {
		ReferenceStackWidget<NullReferenceWidget> w = 
				new ReferenceStackWidget<NullReferenceWidget>(parent);
		w.widget = new NullReferenceWidget(w, SWT.BORDER);
		w.widget.update(100);
		return w;
	}
	
	public void addReference(String name, Class<?> type, Object object) {
		ReferenceWidget widget = new ReferenceWidget(referenceStack, name, type, object);
		widget.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		referenceWidgets.add(widget);
		layout();
		getParent().layout();
	}

	public void removeReference(String name) {
		ReferenceWidget toRemove = null;
		for(ReferenceWidget widget : referenceWidgets)
			if(widget.id.equals(name)) {
				toRemove = widget;
				widget.dispose();
				layout();
				getParent().layout();
			}

		if(toRemove != null)
			referenceWidgets.remove(toRemove);		
	}

	public boolean hasReferences() {
		return referenceWidgets.size() > 0;
	}
	
	public boolean hasReference(String name) {
		for(ReferenceWidget widget : referenceWidgets)
			if(widget.id.equals(name))
				return true;
		return false;
	}
	
	public ReferenceWidget getReferenceWidget(String id) {
		for(ReferenceWidget widget : referenceWidgets)
			if(widget.id.equals(id))
				return widget;
		return null;
	}
	
	public String getFirstReference() {
		if(!referenceWidgets.isEmpty())
			return referenceWidgets.get(0).id;
		else
			return null;
	}

	public void clearReferences() {
		for(ReferenceWidget widget : referenceWidgets)
			widget.dispose();
		
		referenceWidgets.clear();
	}
}
