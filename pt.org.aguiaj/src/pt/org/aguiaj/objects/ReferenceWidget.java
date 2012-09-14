/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.core.AguiaJParam;
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
			new IconWidget(iconAndRef, type).setToolTipText(toolTip);
		
		Menu menu = createMenu();
		setMenu(menu);
		
		LabelWidget label = 
			new LabelWidget.Builder()
			.text(id)
			.small()
			.toolTip(toolTip)
			.linkIf(object != null)
			.create(iconAndRef);
		
		label.setMenu(menu);
		
		if(object != null) {
			label.addObjectHighlightCapability(new ObjectToHighlightProvider() {
				
				@Override
				public Object getObjectToHighlight() {
					return object;
				}
			});
		}
		
		setToolTipText(toolTip);
			
		LabelWidget typeLabel = new LabelWidget.Builder()
		.text("(" + type.getSimpleName() + ")")
		.tiny()
		.create(refBox);
		
		new ArrowWidget(this);

		typeLabel.setMenu(menu);
		
		SWTUtils.setColorRecursively(refBox, AguiaJColor.OBJECT.getColor());
	}

	public void highlight() {
		if(!isDisposed()) {
			SWTUtils.setColorRecursively(refBox, AguiaJColor.HIGHLIGHT.getColor());
			launchReferenceUnhilight();
		}
	}
	
	public void unhighlight() {
		if(!isDisposed())
			SWTUtils.setColorRecursively(refBox, AguiaJColor.OBJECT.getColor());
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
	
	private void launchReferenceUnhilight() {
		Job refUnhilightjob = new Job("reference highlight") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						ReferenceWidget.this.unhighlight();						
					}
				});
				return Status.OK_STATUS;
			}
		};
		refUnhilightjob.schedule(AguiaJParam.HIGHLIGHT_TIMEOUT.getInt() * 1000);
	}
}