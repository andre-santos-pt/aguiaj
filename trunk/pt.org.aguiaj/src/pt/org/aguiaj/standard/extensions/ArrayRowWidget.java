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
package pt.org.aguiaj.standard.extensions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.Reference;
import pt.org.aguiaj.objects.ObjectModel;

class ArrayRowWidget extends Composite {

	private Object array;
	private List<TypeWidget> widgets;
	private boolean hasExtension;
	private List<TypeWidget> extensionWidgets;

	public ArrayRowWidget(
			Composite parent, 
			Class<?> arrayType, 
			Object arrayObject, 
			FieldContainer fieldContainer) {

		super(parent, SWT.NONE);
		assert arrayObject != null;

		this.array = arrayObject;

		hasExtension = WidgetFactory.INSTANCE.hasExtension(arrayType);		
		extensionWidgets = new ArrayList<TypeWidget>();

		int length = Array.getLength(arrayObject);

		GridLayout layout = new GridLayout(length, false);
		setLayout(layout);		
		widgets = new ArrayList<TypeWidget>();

		// EXTENSION WIDGET (OPTIONAL)
		if(hasExtension) {

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
		if(arrayType.isPrimitive()) {
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
					if(!hasExtension)
						fieldContainer.mapArrayFieldToWidget(arrayObject, arrayType, i, widget);
				}
			}
		}

		// INDEX
		for(int i = 0; i < length; i++) {
			//			final int iTmp = i;

			LabelWidget label = new LabelWidget.Builder()
			.text(new Integer(i).toString())
			.small()
			.color(AguiaJColor.GRAY)
			//			.linkIf(!arrayType.isPrimitive())
			.create(this);

			//			if(!arrayType.isPrimitive()) {
			//				label.addHyperlinkAction(new Listener() {
			//					public void handleEvent(Event e) {
			//						JavaCommand cmd = createAccessCommand(iTmp);
			//						ObjectModel.getInstance().execute(cmd);
			//					}
			//				});
			//			}
		}

		fieldContainer.updateFields(arrayObject);
	}


	public void updateFields(Object object) {
		if(hasExtension) {
			for(int i = 0; i < Array.getLength(array); i++) {			
				Object obj = Array.get(array, i);
				extensionWidgets.get(i).update(obj);
			}
		}
		pack();
		layout();
	}

	//	private JavaCommand createAccessCommand(final int iTmp) {
	//		Object obj = Array.get(ArrayRowWidget.this.array, iTmp);
	//		Class<?> type = ArrayRowWidget.this.array.getClass().getComponentType();
	//		Reference ref = ObjectModel.getFirstReference(ArrayRowWidget.this.array);
	//		String refName = ref.name;
	//		String source = ref + "[" + iTmp + "]";					
	//		JavaCommand cmd = new NewReferenceCommand(type, obj, source);
	//		return cmd;
	//	}

}
