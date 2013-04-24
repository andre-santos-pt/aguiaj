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
package pt.org.aguiaj.classes;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.ConstructorInvocationCommand;
import pt.org.aguiaj.core.typewidgets.AbstractTypeWidget;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.objects.ObjectModel;
import pt.org.aguiaj.standard.StandardNamePolicy;

public class ConstructorWidget {

	private Class<?> clazz;
	private Constructor<?> constructor;

	private List<TypeWidget> paramWidgets;

	
	public ConstructorWidget(Composite parent, final Class<?> clazz, final Constructor<?> constructor, FieldContainer fieldContainer) {
		this.clazz = clazz;
		this.constructor = constructor;
		
		paramWidgets = new ArrayList<TypeWidget>();

		final Button newButton = new Button(parent, SWT.PUSH);
		newButton.setText("new");
		FontData data = new FontData(AguiaJParam.FONT.getString(), AguiaJParam.MEDIUM_FONT.getInt(), SWT.NONE);
		Font font = new Font(Display.getDefault(), data);
		newButton.setFont(font);
		newButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				invokeConstructor();
			}
		});
		newButton.setToolTipText(StandardNamePolicy.signature(constructor));
		DocumentationView.getInstance().addDocumentationSupport(newButton, constructor);
		
		
		final Composite argsComposite = new Composite(parent, SWT.NONE);
		//GridLayout layout = new GridLayout(constructor.getParameterTypes().length * 2, false);
		GridLayout layout = new GridLayout(constructor.getParameterTypes().length, false);
		layout.horizontalSpacing = 0;
		argsComposite.setLayout(layout);

		Class<?>[] paramTypes = constructor.getParameterTypes();
		for(int i = 0; i < paramTypes.length; i++) {
			Class<?> paramType = paramTypes[i];

			final TypeWidget widget = WidgetFactory.INSTANCE.createWidget(
					argsComposite, 
					paramType, 
					EnumSet.of(WidgetProperty.PARAMETER, WidgetProperty.MODIFIABLE));

			paramWidgets.add(widget);
			
			if(widget instanceof AbstractTypeWidget) {
				AbstractTypeWidget aWidget = (AbstractTypeWidget) widget;
				aWidget.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));
				aWidget.update(widget.defaultValue());						
			}
			
			// in last param, ENTER triggers constructor invocation
			if(widget.getControl() != null && i == paramTypes.length - 1) {
				widget.getControl().addKeyListener(new KeyAdapter() {
					
					public void keyPressed(KeyEvent event) {
						if(event.keyCode == SWT.CR)
							invokeConstructor();
					}
				});
			}
		}
	}

	private void invokeConstructor() {
		Object[] args = new Object[constructor.getParameterTypes().length];
		for(int i = 0; i < args.length; i++)
			args[i] = paramWidgets.get(i).getObject();
		
		ConstructorInvocationCommand command = new ConstructorInvocationCommand(constructor, args, ObjectModel.getInstance().nextReference(clazz), clazz);
		ObjectModel.getInstance().execute(command);
	}
	
	void setArgs(Object[] args) {
		if(args.length != paramWidgets.size())
			return;
		
		for(int i = 0; i < paramWidgets.size(); i++)
			paramWidgets.get(i).update(args[i]);
	}
}
