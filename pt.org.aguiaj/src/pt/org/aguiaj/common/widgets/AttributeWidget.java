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
package pt.org.aguiaj.common.widgets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.standard.StandardNamePolicy;

public class AttributeWidget extends Composite {
	
	private boolean inherited;
	
	public AttributeWidget(
			final Composite parent, 
			final Field field, 
			final Object object, 
			final FieldContainer fieldContainer, 
			final boolean modifiable,
			final boolean isPrivate) {

		super(parent, SWT.NONE);
		
		setLayout(new GridLayout(2, false));
		
		// there are no inherited static fields
		this.inherited = object != null && Inspector.isInherited(object.getClass(), field);

		createContents(field, object, fieldContainer, modifiable, isPrivate);
	}

	private void createContents(final Field field, final Object object,
			FieldContainer fieldContainer, boolean modifiable, boolean isPrivate) {
		
		String prettyName = StandardNamePolicy.prettyField(field);
		boolean referenceType = !field.getType().isPrimitive();
		
		LabelWidget label = new LabelWidget.Builder()
		.text(prettyName)
		.medium()
		.italicIf(inherited)
		.toolTip(StandardNamePolicy.signature(field))
		.linkIf(referenceType)
		.create(this);

		DocumentationView.getInstance().addDocumentationSupport(label.getControl(), field);
		
		if(referenceType)
			addLinking(field, object, label);

		Object obj = null;
		try {
			field.setAccessible(true);
			obj = field.get(object);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		if(isPrivate && !(field.getType().isPrimitive() || field.getType().equals(String.class)))
			return;

		Set<WidgetProperty> props = EnumSet.of(WidgetProperty.ATTRIBUTE);
		if(!Modifier.isFinal(field.getModifiers()) && modifiable)
			props.add(WidgetProperty.MODIFIABLE);
		
		final TypeWidget fieldWidget = 
			WidgetFactory.INSTANCE.createWidget(
					this, 
					field.getType(), 					
					props);

		if(fieldWidget instanceof AbstractTypeWidget)
			((AbstractTypeWidget) fieldWidget).setAttribute(field, object);		

		fieldContainer.mapToWidget(field, fieldWidget);

		try {
			fieldWidget.update(obj);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addLinking(final Field field, final Object object,
			LabelWidget label) {
		label.addHyperlinkAction(new Listener () {
			public void handleEvent(Event event) {
				Object value = null;
				try {
					value = field.get(object);
				} catch (Exception e) {
					e.printStackTrace();
				} 

				String source = "." + field.getName();					
				if(object == null)
					source = field.getDeclaringClass().getSimpleName() + source;
				else
					source = ObjectModel.getFirstReference(object).name + source;

				String refName = ObjectModel.aspectOf().nextReference(field.getType());

				new NewReferenceCommand(field.getType(), value, source, refName).execute();
			}
		});
		
		label.addObjectHighlightCapability(new ObjectToHighlightProvider() {

			@Override
			public Object getObjectToHighlight() {
				try {
					return field.get(object);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}
}
