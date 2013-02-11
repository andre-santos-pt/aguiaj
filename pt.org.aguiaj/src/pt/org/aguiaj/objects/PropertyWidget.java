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

import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.Highlightable;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.standard.StandardNamePolicy;


public class PropertyWidget implements Highlightable {
	private LabelWidget label;

	public PropertyWidget(Composite parent, final Object object, final Method propertyMethod, FieldContainer fieldContainer) {
		boolean inherited = Inspector.isInherited(object.getClass(), propertyMethod);
		boolean overriding = Inspector.isOverriding(object.getClass(), propertyMethod);
		boolean returnsReferenceType = !propertyMethod.getReturnType().isPrimitive();

		String name = StandardNamePolicy.prettyPropertyName(propertyMethod);
		String toolTip = StandardNamePolicy.getMethodToolTip(object, propertyMethod);

		label = new LabelWidget.Builder()
		.text(name)
		.medium()
		.italicIf(inherited)
		.boldIf(overriding)
		.toolTip(toolTip)
		.linkIf(returnsReferenceType)
		.create(parent);

		DocumentationView.getInstance().addDocumentationSupport(label.getControl(), propertyMethod);


		if(returnsReferenceType) {		
			label.addHyperlinkAction(new Listener () {
				public void handleEvent(Event event) {
					String ref = ObjectModel.getFirstReference(object).name;
					MethodInvocationCommand command = new MethodInvocationCommand(object, ref, propertyMethod, new Object[0], new String[0]);
					ObjectModel.getInstance().execute(command);
				}
			});

			label.addObjectHighlightCapability(new ObjectToHighlightProvider() {
				@Override
				public Object getObjectToHighlight() {
					Reference ref = ObjectModel.getFirstReference(object);
					Object obj = null;
					if(ref != null) {
						MethodInvocationCommand command = new MethodInvocationCommand(object, ref.name, propertyMethod, new Object[0], new String[0]);
						command.execute();
						obj = command.getResultingObject();
					}
					return obj;	
				}
			});
		}

		Composite row = new Composite(parent, SWT.NONE);
		row.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		List<TypeWidget> widgets = WidgetFactory.INSTANCE.createWidgets(
				row, 
				propertyMethod.getReturnType(),
				EnumSet.of(WidgetProperty.PROPERTY));				

		for(TypeWidget w : widgets)
			fieldContainer.mapToWidget(propertyMethod, w);
	}

	@Override
	public void highlight() {
		label.highlight();		
	}

	@Override
	public void unhighlight() {
		label.unhighlight();		
	}
}

