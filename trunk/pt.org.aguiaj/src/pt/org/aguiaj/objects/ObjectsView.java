/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.aspects.CommandMonitor;
import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.DragNDrop;
import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.NullReferenceWidget;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;
import pt.org.aguiaj.core.commands.RemoveObjectsCommand;
import pt.org.aguiaj.extensibility.AguiaJContribution;


public class ObjectsView extends ViewPart {

	private static final int padding = 20;

	private static ObjectsView instance;

	private Composite area;
	private ScrolledComposite scrl;

	private Map<Object, ObjectWidget> widgetsTable;
	private Map<Object, ReferenceObjectPairWidget> refAndObjectPairsTable;

	private ObjectWidget highlighted;

	private Composite nullReferencesStack;

	private Composite nulls;
	private NullReferenceWidget nullRefWidget;
	
	private Map<String, ReferenceWidget> nullReferenceMap;


	public ObjectsView() {
		instance = this;	
	}

	public static ObjectsView getInstance() {
		if(instance == null)			
			SWTUtils.showView(AguiaJContribution.OBJECTS_VIEW);			
			
		return instance;
	}

	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		widgetsTable = new IdentityHashMap<Object, ObjectWidget>();
		refAndObjectPairsTable = new IdentityHashMap<Object, ReferenceObjectPairWidget>();
		nullReferenceMap = new HashMap<String, ReferenceWidget>();
		
		scrl = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);		
		area = new Composite(scrl, SWT.NONE);

		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.spacing = padding;
		layout.marginTop = padding;
		layout.marginLeft = padding;
		layout.marginRight = padding;
		layout.marginBottom = padding;

		area.setLayout(layout);
		area.setBackground(AguiaJColor.OBJECT_AREA.getColor());
		area.setToolTipText("Object Area (interact with the objects by pressing the buttons and clicking the links)");

		scrl.setContent(area);
		scrl.setExpandVertical(true);
		scrl.setExpandHorizontal(true);
		scrl.setAlwaysShowScrollBars(true);
		
		nulls = new Composite(area, SWT.NONE);
		nulls.setLayout(new RowLayout(SWT.HORIZONTAL));
		nulls.setBackground(AguiaJColor.OBJECT_AREA.getColor());
		nullReferencesStack = new Composite(nulls, SWT.NONE);
		nullReferencesStack.setBackground(AguiaJColor.OBJECT_AREA.getColor());
		nullReferencesStack.setLayout(new RowLayout(SWT.VERTICAL));
		
		nullRefWidget = new NullReferenceWidget(nulls, SWT.BORDER);
		nullRefWidget.update(100);
		nullRefWidget.setVisible(false);
		
		addActions();

		DragNDrop.addFileDragNDropSupportObjectArea(area);
	}

	
	// update to last created widget ...
	public void updateLayout(String reference) {
		area.layout();	

		int maxWidth = maxWidth();

		scrl.setMinSize(area.computeSize(maxWidth + padding * 2, SWT.DEFAULT));

		if(reference != null) {
			ReferenceObjectPairWidget widget = getRefAndObjectPairWidget(reference);
			if(widget != null) {
				Point loc = widget.getLocation();		
				if(isDistant(loc, scrl.getOrigin()))
					scrl.setOrigin(loc);
			}
			else {
				scrl.setOrigin(0, 0);
			}
		}
	}

	private static boolean isDistant(Point p1, Point p2) {
		int a = p1.y;
		int b = p2.y;

		return (a > b ? a - b : b - a) > 400; // TODO : calc value
	}

	private int maxWidth() {
		int max = 0;
		for(ReferenceObjectPairWidget widget : refAndObjectPairsTable.values()) {
			if(widget.getSize().x > max)
				max = widget.getSize().x;
		}
		return max;
	}

	private void addActions() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();

		Action refreshAction = new Action("Refresh") {
			public void run() { 
				try {
					new ReloadClassesCommand().execute(null);
				} catch (ExecutionException e) {					
					e.printStackTrace();
				}
			}
		};
		refreshAction.setImageDescriptor(AguiaJImage.REFRESH.getImageDescriptor());
		toolbarManager.add(refreshAction);

		Action removeAllAction = new Action("Remove all") {
			public void run() { 	
				List<Object> objs = Arrays.asList(ObjectModel.aspectOf().getAllObjects());
				new RemoveObjectsCommand(objs).execute();

				List<Reference> nullRefs = ObjectModel.aspectOf().getNullReferences();
				for(Reference ref : nullRefs)
					ObjectsView.getInstance().removeNullReference(ref.name);

				ObjectsView.getInstance().unhighlight();
				CommandMonitor.aspectOf().clearStack();
			}
		};
		removeAllAction.setImageDescriptor(AguiaJImage.DELETE.getImageDescriptor());
		toolbarManager.add(removeAllAction);

		Action removeDeadAction = new Action("Remove dead objects") {
			public void run() { 
				List<Object> objs = Arrays.asList(ObjectModel.aspectOf().getDeadObjects());
				new RemoveObjectsCommand(objs).execute();
				updateObjectWidgets();
			}
		};
		removeDeadAction.setImageDescriptor(AguiaJImage.DELETEDEAD.getImageDescriptor());
		toolbarManager.add(removeDeadAction);			
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		area.setFocus();
	}




	public ObjectWidget getObjectWidget(Object object) {		
		return widgetsTable.get(object);
	}


	public ObjectWidget getObjectWidgetByReference(String reference) {
		for(ReferenceObjectPairWidget widget : refAndObjectPairsTable.values())
			if(widget.hasReference(reference))
				return widget.widget;

		return null;
	}

	public void addObjectWidget(Object object, String reference, Class<?> referenceType) {
		assert object != null;

		ObjectWidget widget = getObjectWidget(object);

		if(widget == null) {
			ReferenceObjectPairWidget pair =  new ReferenceObjectPairWidget(area, reference, object);
			widget = pair.widget;
			widgetsTable.put(object, widget);
			refAndObjectPairsTable.put(object, pair);	
		}

		addReference(referenceType, reference, object);
		updateLayout(reference);
	}




	public void addReference(Class<?> type, String reference, Object object) {
		if(object == null) {
			if(!nullReferenceMap.containsKey(reference)) {
				ReferenceObjectPairWidget widget = getRefAndObjectPairWidget(reference);
				if(widget != null)
					widget.removeReference(reference);
				addNullReference(type, reference);
			}
		}
		else {
			addNonNullReference(type, reference, object);
		}

		updateLayout(reference);
	}

	private void addNonNullReference(Class<?> type, String reference, Object object) {		
		if(nullReferenceMap.containsKey(reference))
			removeNullReference(reference);
		else if(ObjectModel.aspectOf().isReferenceInUse(reference))
			removeReference(reference);

		ReferenceObjectPairWidget widget = refAndObjectPairsTable.get(object);
		if(widget != null)
			widget.addReference(reference, type, object);				
	}

	private void addNullReference(Class<?> type, String reference) {
		ReferenceWidget widget = new ReferenceWidget(nullReferencesStack, reference, type, null);
//		widget.highlight();
		nullReferenceMap.put(reference, widget);
		nullRefWidget.setVisible(true);
		nulls.layout();
	}


	public void removeReference(String id) {
		ReferenceObjectPairWidget widget = getRefAndObjectPairWidget(id);
		if(widget != null)
			widget.removeReference(id);
	}	

	public void removeNullReference(String id) {
		if(nullReferenceMap.containsKey(id)) {
			nullReferenceMap.get(id).dispose();
			nullReferenceMap.remove(id);
			if(nullReferenceMap.keySet().size() == 0)
				nullRefWidget.setVisible(false);
			nulls.layout();
			area.layout();
		}
	}

	public List<String> getReferencesForExpandedOperationsObjects() {
		List<String> refs = new ArrayList<String>();

		for(ReferenceObjectPairWidget widget : refAndObjectPairsTable.values()) {
			String ref = widget.getFirstReference();
			if(ref != null && widget.widget.isOperationsVisible()) {
				refs.add(ref);
			}
		}

		return refs;
	}

	public List<String> getReferencesForExpandedPrivatesObjects() {
		List<String> refs = new ArrayList<String>();

		for(ReferenceObjectPairWidget widget : refAndObjectPairsTable.values()) {
			String ref = widget.getFirstReference();
			if(ref != null && widget.widget.isPrivateAttributesVisible()) {
				refs.add(ref);
			}
		}

		return refs;
	}


	private ReferenceObjectPairWidget getRefAndObjectPairWidget(String refId) {
		for(ReferenceObjectPairWidget widget : refAndObjectPairsTable.values())
			if(widget.hasReference(refId))
				return widget;
		return null;
	}


	public void removeAll() {
		List<Object> allObjects = new ArrayList<Object>();
		allObjects.addAll(widgetsTable.keySet());

		for(String ref : nullReferenceMap.keySet().toArray(new String[nullReferenceMap.keySet().size()]))
			removeNullReference(ref);

		new RemoveObjectsCommand(allObjects).execute();
	}

	public void remove(Object object) {
		ObjectWidget widget = getObjectWidget(object);

		if(widget != null) {
			widget.dispose();
			widgetsTable.remove(object);

			refAndObjectPairsTable.get(object).dispose();
			refAndObjectPairsTable.remove(object);

			if(highlighted == widget)
				highlighted = null;
		}

		updateLayout(null);
	}


	public void highlight(Object object) {
		assert object != null;

		ObjectWidget widget = getObjectWidget(object);

		if(widget != null) {
			if(highlighted != null)
				highlighted.unhighlight();

			widget.highlight();
			highlighted = widget;
			launchObjectUnhilight();
		}
	}

	public void unhighlight() {
		if(objectUnhilightjob != null) {
			synchronized (objectUnhilightjob) {													
				objectUnhilightjob = null;
			}
			objectUnhilightjob = null;
		}

		if(highlighted != null) {
			highlighted.unhighlight();
			highlighted.layout();
		}
		highlighted = null;
	}

	public void updateObjectWidgets() {
		for(ObjectWidget widget : widgetsTable.values())
			widget.updateFields();

		updateLayout(null);
	}

	@Override
	public void dispose() {		
		super.dispose();
		instance = null;
	}

	public void show() {
		scrl.setVisible(true);
	}

	public void hide() {
		scrl.setVisible(false);
	}


	private Job objectUnhilightjob;

	private void launchObjectUnhilight() {
		if(objectUnhilightjob != null)
			synchronized (objectUnhilightjob) {
				objectUnhilightjob.cancel();
			}

		objectUnhilightjob = new Job("object highlight") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						ObjectsView.getInstance().unhighlight();						
					}
				});
				return Status.OK_STATUS;
			}
		};
		objectUnhilightjob.schedule(AguiaJParam.HIGHLIGHT_TIMEOUT.getInt() * 1000);
	}
}
