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

import static com.google.common.collect.Maps.newHashMap;

import java.util.EnumSet;
import java.util.IdentityHashMap;
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

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.DragNDrop;
import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.NullReferenceWidget;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;
import pt.org.aguiaj.core.commands.RemoveAllObjectsCommand;
import pt.org.aguiaj.core.commands.RemoveDeadObjectsCommand;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.extensibility.AguiaJContribution;

public class ObjectsView extends ViewPart {

	private static final int padding = 20;

	private static ObjectsView instance;

	private Composite area;
	private ScrolledComposite scrl;

	private Map<Object, ObjectWidget> widgetsTable;
	private Map<Object, ReferenceStackWidget<ObjectWidget>> refStackTable;

	private ObjectWidget highlighted;

	private ReferenceStackWidget<NullReferenceWidget> nullStack;


	private class ObjectListener extends ObjectModel.EventListenerAdapter {

		@Override
		public void newObjectEvent(Object obj) {
			addDeadObjectWidget(obj);
		}

		@Override
		public void removeObjectEvent(Object obj) {
			remove(obj);

		}

		@Override
		public void newReferenceEvent(Reference ref) {
			if(ref.object != null)
				addObjectWidget(ref.object, ref.name, ref.type);
			else
				nullStack.addReference(ref.name, ref.type, null);
			
			nullStack.setVisible(nullStack.hasReferences());
		}

		@Override
		public void changeReferenceEvent(Reference ref) {
			addReference(ref.type, ref.name, ref.object);
			nullStack.setVisible(nullStack.hasReferences());
		}

		@Override
		public void removeReferenceEvent(Reference ref) {
			removeReference(ref.name);
			nullStack.setVisible(nullStack.hasReferences());
		}

		@Override
		public void commandExecuted(JavaCommand cmd) {
			updateObjectWidgets();
		}

		@Override
		public void clearAll() {
			clearAllWidgets();
			nullStack.setVisible(false);
		}
	}

	public ObjectsView() {
		instance = this;
		ObjectModel.getInstance().addEventListener(new ObjectListener());
	}

	public static ObjectsView getInstance() {
		if(instance == null)			
			SWTUtils.showView(AguiaJContribution.OBJECTS_VIEW);			

		return instance;
	}

	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		widgetsTable = new IdentityHashMap<Object, ObjectWidget>();
		refStackTable = new IdentityHashMap<Object, ReferenceStackWidget<ObjectWidget>>();

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

		nullStack = ReferenceStackWidget.newNull(area);
		nullStack.setVisible(false);

		addActions();

		DragNDrop.addFileDragNDropSupportObjectArea(area);
	}

	private void clearAllWidgets() {
		nullStack.clearReferences();

		for(ReferenceStackWidget<ObjectWidget> w : refStackTable.values())
			w.dispose();

		refStackTable.clear();
		widgetsTable.clear();
		area.layout();
	}

	// update to last created widget ...
	public void updateLayout(String reference) {
		area.layout();	

		int maxWidth = maxWidth();

		scrl.setMinSize(area.computeSize(maxWidth + padding * 2, SWT.DEFAULT));

		if(reference != null) {
			ReferenceStackWidget<ObjectWidget> widget = getRefAndObjectPairWidget(reference);
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
		for(ReferenceStackWidget<ObjectWidget> widget : refStackTable.values()) {
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
				new RemoveAllObjectsCommand().execute();
			}
		};

		removeAllAction.setImageDescriptor(AguiaJImage.DELETE.getImageDescriptor());
		toolbarManager.add(removeAllAction);

		Action removeDeadAction = new Action("Garbage collection") {
			public void run() { 
				new RemoveDeadObjectsCommand().execute();
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
		for(ReferenceStackWidget<ObjectWidget> widget : refStackTable.values())
			if(widget.hasReference(reference))
				return widget.getWidget();

		return null;
	}




	private void addObjectWidget(Object object, String reference, Class<?> referenceType) {
		assert object != null;
		assert reference != null;
		assert referenceType != null;

		ObjectWidget widget = getObjectWidget(object);
		if(widget == null) {
			ReferenceStackWidget<ObjectWidget> pair =  ReferenceStackWidget.newObject(area, object);
			widgetsTable.put(object, pair.getWidget());
			refStackTable.put(object, pair);	
		}

		addReference(referenceType, reference, object);
		updateLayout(reference);
	}

	public void addDeadObjectWidget(Object object) {
		if(object == null)
			return;

		ObjectWidget widget = getObjectWidget(object);
		if(widget == null) {
			ReferenceStackWidget<ObjectWidget> pair =  ReferenceStackWidget.newDeadObject(area, object);
			widget = pair.getWidget();
			widgetsTable.put(object, widget);
			refStackTable.put(object, pair);
		}
		updateLayout(null);
	}


	private void addReference(Class<?> type, String reference, Object object) {
		if(object == null) {
			ReferenceStackWidget<ObjectWidget> widget = getRefAndObjectPairWidget(reference);
			if(widget != null)
				widget.removeReference(reference);

			if(!nullStack.hasReference(reference))
				nullStack.addReference(reference, type, null);
		}
		else {
			addNonNullReference(type, reference, object);
		}

		updateLayout(reference);
	}

	private void addNonNullReference(Class<?> type, String name, Object object) {		
		if(nullStack.hasReference(name))
			nullStack.removeReference(name);
		else if(ObjectModel.getInstance().isReferenceInUse(name))
			removeReference(name);

		ReferenceStackWidget<ObjectWidget> widget = refStackTable.get(object);
		if(widget != null)
			widget.addReference(name, type, object);				
	}


	private void removeReference(String name) {
		ReferenceStackWidget<ObjectWidget> widget = getRefAndObjectPairWidget(name);
		if(widget != null)
			widget.removeReference(name);
		else {
			nullStack.removeReference(name);
		}
	}	

	public Map<String, EnumSet<ObjectWidget.Section>> getObjectExpandedSections() {
		Map<String, EnumSet<ObjectWidget.Section>> map = newHashMap();
		for(ReferenceStackWidget<ObjectWidget> w : refStackTable.values()) {
			String ref = w.getFirstReference();
			if(ref != null) {
				ObjectWidget widget = w.getWidget();
				map.put(ref, EnumSet.copyOf(widget.getExpandedSections()));
			}
		}
		return map;
	}
	
//	public List<String> getReferencesForExpandedOperationsObjects() {
//		List<String> refs = new ArrayList<String>();
//
//		for(ReferenceStackWidget<ObjectWidget> w : refStackTable.values()) {
//			String ref = w.getFirstReference();
//			if(ref != null && w.getWidget().isOperationsVisible()) {
//				refs.add(ref);
//			}
//		}
//
//		return refs;
//	}
//
//	public List<String> getReferencesForExpandedPrivatesObjects() {
//		List<String> refs = new ArrayList<String>();
//
//		for(ReferenceStackWidget<ObjectWidget> widget : refStackTable.values()) {
//			String ref = widget.getFirstReference();
//			if(ref != null && widget.getWidget().isPrivateAttributesVisible()) {
//				refs.add(ref);
//			}
//		}
//
//		return refs;
//	}
//
//
//	public List<String> getReferencesForExpandedPropertiesObjects() {
//		List<String> refs = new ArrayList<String>();
//
//		for(ReferenceStackWidget<ObjectWidget> widget : refStackTable.values()) {
//			String ref = widget.getFirstReference();
//			if(ref != null && widget.getWidget().isPropertiesVisible()) {
//				refs.add(ref);
//			}
//		}
//
//		return refs;
//	}
//	
//	public List<String> getReferencesForExpandedAttributesObjects() {
//		List<String> refs = new ArrayList<String>();
//
//		for(ReferenceStackWidget<ObjectWidget> widget : refStackTable.values()) {
//			String ref = widget.getFirstReference();
//			if(ref != null && widget.getWidget().isPropertiesVisible()) {
//				refs.add(ref);
//			}
//		}
//
//		return refs;
//	}
	
	private ReferenceStackWidget<ObjectWidget> getRefAndObjectPairWidget(String refId) {
		for(ReferenceStackWidget<ObjectWidget> widget : refStackTable.values())
			if(widget.hasReference(refId))
				return widget;
		return null;
	}


	private void remove(Object object) {
		ObjectWidget widget = getObjectWidget(object);

		if(widget != null) {
			widget.dispose();
			widgetsTable.remove(object);

			refStackTable.get(object).dispose();
			refStackTable.remove(object);

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

	private void updateObjectWidgets() {
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
