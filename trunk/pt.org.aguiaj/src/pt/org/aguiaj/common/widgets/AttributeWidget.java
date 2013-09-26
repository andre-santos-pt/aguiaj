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
package pt.org.aguiaj.common.widgets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.core.InspectionPolicy;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.NewDeadObjectCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.documentation.DocumentationLinking;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.objects.ObjectModel;
import pt.org.aguiaj.standard.StandardNamePolicy;


public class AttributeWidget {

	private boolean inherited;
	
	public AttributeWidget(
			final Composite parent, 
			final Field field, 
			final Object object, 
			final FieldContainer fieldContainer, 
			final boolean modifiable,
			final boolean isPrivate) {

//		super(parent, SWT.NONE);

//		setLayout(new GridLayout(2, false));

		// there are no inherited static fields
		this.inherited = object != null && Inspector.isInherited(object.getClass(), field);

		createContents(parent, field, object, fieldContainer, modifiable, isPrivate);
	}

	private void createContents(Composite parent, final Field field, final Object object,
			FieldContainer fieldContainer, boolean modifiable, boolean isPrivate) {

		String prettyName = StandardNamePolicy.prettyField(field);
		boolean referenceType = !field.getType().isPrimitive();

		String toolTip = StandardNamePolicy.signature(field);
		if(inherited)
			toolTip += " (inherited from " + field.getDeclaringClass().getSimpleName() + ")";
			
		LabelWidget label = new LabelWidget.Builder()
		.text(prettyName)
		.medium()
		.italicIf(inherited)
		.toolTip(toolTip)
		.linkIf(referenceType)
		.create(parent);

		DocumentationLinking.add(label.getControl(), field);

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

		Set<WidgetProperty> props = EnumSet.of(WidgetProperty.ATTRIBUTE);
		if(!Modifier.isFinal(field.getModifiers()) && modifiable)
			props.add(WidgetProperty.MODIFIABLE);

		if(isPrivate)
			props.add(WidgetProperty.NO_EXTENSION);
		
		Composite row = new Composite(parent, SWT.NONE);
		row.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		List<TypeWidget> widgets = 	WidgetFactory.INSTANCE.createWidgets(row, field.getType(), props);

		for(TypeWidget fieldWidget : widgets) {
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
	}

	private void addLinking(final Field field, final Object object, LabelWidget label) {

		label.addHyperlinkAction(new Listener () {
			public void handleEvent(Event event) {
				Object value = null;
				try {
					value = field.get(object);
				} catch (Exception e) {
					e.printStackTrace();
				} 

				InspectionPolicy policy = ClassModel.getInspector().getPolicy();
				if(policy.isInstanceFieldVisible(field) ||
						policy.isStaticFieldVisible(field)) {

					String source = "." + field.getName();					
					if(object == null)
						source = field.getDeclaringClass().getSimpleName() + source;
					else
						source = ObjectModel.getFirstReference(object).name + source;

					String refName = ObjectModel.getInstance().nextReference(field.getType());

					JavaCommand cmd = new NewReferenceCommand(field.getType(), value, source, refName);
					ObjectModel.getInstance().execute(cmd);
				}
				else {
					new NewDeadObjectCommand(value).execute();
				}

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
