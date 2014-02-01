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
package pt.org.aguiaj.objects;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.TypeMemberMouseTrackAdapter;
import pt.org.aguiaj.core.commands.RemoveReferenceCommand;


public class ReferenceWidget extends Composite {

	public final String id;
	private Composite refBox;

	public ReferenceWidget(Composite parent, final String id, final Class<?> type, final Object object) {
		super(parent, SWT.NONE);
		this.id = id;

		boolean isPolymorphic = ClassModel.getInstance().isPolymorphic(type);

		String toolTip = "Reference of type " + type.getSimpleName();
		if(isPolymorphic)
			toolTip = toolTip + " (Polymorphic)";

		setBackground(AguiaJColor.OBJECT_AREA.getColor());
		setLayout(new RowLayout(SWT.HORIZONTAL));

		refBox = new Composite(this, SWT.BORDER);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.marginBottom = 0;
		layout.spacing = 0;
		layout.marginTop = 0;
		layout.marginRight = 5;
		refBox.setLayout(layout);

		Composite iconAndRef = new Composite(refBox, SWT.NONE);
		iconAndRef.setLayout(new RowLayout(SWT.HORIZONTAL));

		if(isPolymorphic)
			IconWidget.createForRowLayout(iconAndRef, type).setToolTipText(toolTip);

		Menu menu = createMenu();
		setMenu(menu);
		refBox.setMenu(menu);

		LabelWidget label = new LabelWidget.Builder()
		.text(id)
		.small()
		.toolTip(toolTip)
		.linkIf(object != null)
		.create(iconAndRef);

		label.getControl().setMenu(menu);

		if(object != null) {
			ObjectWidget widget = ObjectsView.getInstance().getObjectWidget(object);
			label.getControl().addMouseTrackListener(new TypeMemberMouseTrackAdapter(widget, type));
		}

		setToolTipText(toolTip);

		LabelWidget typeLabel = new LabelWidget.Builder()
		.text("(" + type.getSimpleName() + ")")
		.tiny()
		.create(refBox);

		ArrowWidget arrow = new ArrowWidget(this);
		arrow.setMenu(menu);

		typeLabel.getControl().setMenu(menu);
	}


	private Menu createMenu() {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem removeItem = new MenuItem(menu, SWT.PUSH);
		removeItem.setText("Remove");
		removeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				new RemoveReferenceCommand(id).execute();
			}
		});

		return menu;
	}

	private static class ArrowWidget extends Composite {
		public ArrowWidget(Composite parent) {
			super(parent, SWT.NONE);
			setLayout(new FillLayout());
			setBackgroundImage(AguiaJImage.REFARROW.getImage());
			setLayoutData(new RowData(30, 20));
		}
	}

}
