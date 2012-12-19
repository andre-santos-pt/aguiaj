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
package pt.org.aguiaj.standard.extensions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.objects.ObjectModel;

class ArrayRowWidget extends Composite {

	private Object array;

	private List<TypeWidget> widgets;
	private List<TypeWidget> extensionWidgets;

	private boolean hasExtensionWidgets;

	public ArrayRowWidget(
			Composite parent, 
			Class<?> arrayType, 
			Object arrayObject, 
			FieldContainer fieldContainer) {
		
		super(parent, SWT.NONE);
		assert arrayObject != null;

		this.array = arrayObject;

		boolean hasExtension = WidgetFactory.INSTANCE.hasExtension(arrayType);		
		boolean hasToString = !arrayType.isPrimitive() && ReflectionUtils.declaresToString(arrayType);

		hasExtensionWidgets = (hasExtension || hasToString); 

		if(hasExtensionWidgets)
			extensionWidgets = new ArrayList<TypeWidget>();

		int length = Array.getLength(arrayObject);

		GridLayout layout = new GridLayout(length, false);
		setLayout(layout);		
		widgets = new ArrayList<TypeWidget>();

		// EXTENSION WIDGET (OPTIONAL)
		if(hasExtensionWidgets) {
			for(int i = 0; i < length; i++) {
				TypeWidget widget = WidgetFactory.INSTANCE.createWidget(
						this, 
						arrayType, 
						EnumSet.of(WidgetProperty.PROPERTY));
				extensionWidgets.add(widget);
				fieldContainer.mapArrayFieldToWidget(arrayObject, arrayType, i, widget);
				
			}
		}

		// NORMAL WIDGET
		for(int i = 0; i < length; i++) {
			TypeWidget widget = WidgetFactory.INSTANCE.createWidget(
					this, 
					arrayType, 
					EnumSet.of(
							WidgetProperty.ARRAYPOSITION, 
							WidgetProperty.MODIFIABLE, 
							WidgetProperty.NO_EXTENSION));
			
			if(widget instanceof AbstractTypeWidget) {					
				((AbstractTypeWidget) widget).setArrayPosition(arrayObject, i);
				widgets.add(widget);
				if(!hasExtensionWidgets)
					fieldContainer.mapArrayFieldToWidget(arrayObject, arrayType, i, widget);
			}
		}

		// INDEX
		for(int i = 0; i < length; i++) {
			final int iTmp = i;

			LabelWidget label = new LabelWidget.Builder()
			.text(new Integer(i).toString())
			.tiny()
			.color(AguiaJColor.GRAY)
			.linkIf(!arrayType.isPrimitive())
			.create(this);

			if(!arrayType.isPrimitive()) {
				label.addHyperlinkAction(new Listener() {
					public void handleEvent(Event e) {
						JavaCommand cmd = createAccessCommand(iTmp);
						ObjectModel.getInstance().execute(cmd);
					}
				});
				label.addObjectHighlightCapability(new ObjectToHighlightProvider() {						
					@Override
					public Object getObjectToHighlight() {
						return Array.get(ArrayRowWidget.this.array, iTmp);
					}
				});
			}
		}

		fieldContainer.updateFields(arrayObject);
		if(hasExtensionWidgets) {
			int i = 0;
			for(TypeWidget widget : extensionWidgets) {
				Control control = widget.getControl();
				if(control != null)
					addDragSupport(control, arrayType, i);
				i++;
			}
		}
	}

	private void addDragSupport(Control control, final Class<?> arrayType, final int index) {
		assert control != null;
		
		int operations = DND.DROP_MOVE | DND.DROP_COPY;
		DragSource source = new DragSource(control, operations);
		Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
			}
			public void dragSetData(DragSourceEvent event) {
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					event.data = createAccessCommand(index).getJavaInstruction();
				}
			}

			public void dragFinished(DragSourceEvent event) {
				// If a move operation has been performed, remove the data
				// from the source
			}
		});
	}

	public void updateFields(Object object) {
		for(int i = 0; i < Array.getLength(array); i++) {			
			if(hasExtensionWidgets) {
				Object obj = Array.get(array, i);
				extensionWidgets.get(i).update(obj);
			}
		}
		pack();
		layout();
	}

	private JavaCommand createAccessCommand(final int iTmp) {
		Object obj = Array.get(ArrayRowWidget.this.array, iTmp);
		Class<?> type = ArrayRowWidget.this.array.getClass().getComponentType();
		String ref = ObjectModel.getFirstReference(ArrayRowWidget.this.array).name;
		String source = ref + "[" + iTmp + "]";					
		JavaCommand cmd = new NewReferenceCommand(type, obj, source);
		return cmd;
	}
}
