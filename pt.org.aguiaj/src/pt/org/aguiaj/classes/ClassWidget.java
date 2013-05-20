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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.widgets.AttributeWidget;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.common.widgets.MethodWidget;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.documentation.DocumentationLinking;
import pt.org.aguiaj.core.documentation.DocumentationView;
import pt.org.aguiaj.objects.ObjectModel;
import pt.org.aguiaj.standard.StandardNamePolicy;


public class ClassWidget extends FieldContainer {

	private List<Field> staticFields;
	private Map<Constructor<?>, ConstructorWidget> constructorMap;
	private Map<Method, MethodWidget> methodMap;
	
	public ClassWidget(Composite parent, final Class<?> clazz) {
		super(parent, SWT.BORDER);
		
		constructorMap = new HashMap<Constructor<?>, ConstructorWidget>();
		methodMap = new HashMap<Method, MethodWidget>();
		
		FormLayout layout = new FormLayout();
		layout.marginBottom = 5;
		setLayout(layout);
		
		Composite classHeader = new Composite(this, SWT.NONE);
		classHeader.setLayout(new RowLayout(SWT.HORIZONTAL));

		if(ClassModel.getInstance().hasSubClasses(clazz)) 
			new IconWidget(classHeader, clazz);

		LabelWidget classNameLabel = new LabelWidget.Builder()
			.text(StandardNamePolicy.prettyClassName(clazz))
			.big()
			.create(classHeader);
		
		DocumentationLinking.add(classNameLabel.getControl(), clazz);
		
		if(ClassModel.getInspector().isStaticClass(clazz))
			new LabelWidget.Builder()
				.text("(static)")
				.small()
				.create(classHeader);
				
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 5);
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(100, -5);
		classHeader.setLayoutData(labelData);
		
		staticFields = ClassModel.getInspector().getVisibleStaticAttributes(clazz);
			
		Composite constructorsOrContantsGroup = null;
		if(clazz.isEnum())
			constructorsOrContantsGroup = createConstantsGroup(this, clazz);
		else			
			constructorsOrContantsGroup = createConstructorsGroup(this, clazz);

		if(constructorsOrContantsGroup != null) {
			FormData data = new FormData();
			data.top = new FormAttachment(classHeader, 5);
			data.left = new FormAttachment(0, 5);
			data.right = new FormAttachment(100, -5);
			constructorsOrContantsGroup.setLayoutData(data);
		}
		
		Group staticFieldsGroup = createStaticFieldsGroup(this, clazz);		
		if(staticFieldsGroup != null) {
			FormData data = new FormData();
			data.top = new FormAttachment(constructorsOrContantsGroup != null ? constructorsOrContantsGroup : classHeader, 5);
			data.left = new FormAttachment(0, 5);
			data.right = new FormAttachment(100, -5);
			staticFieldsGroup.setLayoutData(data);
		}
		
		Group staticMethodsGroup = createStaticMethodsGroup(this, clazz);
		if(staticMethodsGroup != null) {
			FormData data = new FormData();
			data.top = new FormAttachment(staticFieldsGroup != null ? staticFieldsGroup :  constructorsOrContantsGroup != null ? constructorsOrContantsGroup : classHeader, 5);
			data.left = new FormAttachment(0, 5);
			data.right = new FormAttachment(100, -5);
			staticMethodsGroup.setLayoutData(data);
		}
		
		updateFields();	
	}

	private Group createConstantsGroup(Composite parent, final Class<?> clazz) {
		Group constantsGroup = new Group(parent, SWT.NONE);
		constantsGroup.setText(UIText.ENUM_CONSTANTS.get());
		constantsGroup.setLayout(new RowLayout(SWT.VERTICAL));
		for(final Field field : clazz.getFields()) {
			if(field.isEnumConstant()) {
				field.setAccessible(true);
				LabelWidget label = new LabelWidget.Builder()
					.text(field.getName())
					.medium()
					.toolTip(clazz.getSimpleName() + "." + field.getName())
					.link()
					.create(constantsGroup);
				
				DocumentationLinking.add(label.getControl(), field);
				
				label.addHyperlinkAction(new Listener () {
					public void handleEvent(Event event) {
						try {
							Object enumConst = field.get(null);
							String source = clazz.getSimpleName() + "." + field.getName();
							String ref = ObjectModel.getInstance().nextReference(field.getType());
							ObjectModel.getInstance().execute(new NewReferenceCommand(clazz, enumConst, source, ref));
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				});
				
				label.addObjectHighlightCapability(new ObjectToHighlightProvider() {
					
					@Override
					public Object getObjectToHighlight() {
						try {
							return field.get(null);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
				});
			}
		}
		
		return constantsGroup;
	}

	private Group createStaticFieldsGroup(Composite parent, final Class<?> clazz) {	
		if(!staticFields.isEmpty()) {
			Group staticAttributesGroup = new Group(parent, SWT.NONE);
			staticAttributesGroup.setText(UIText.STATIC_FIELDS.get());
			RowLayout layout = new RowLayout(SWT.VERTICAL);
			staticAttributesGroup.setLayout(layout);			
			
			for(Field field : staticFields)	
				new AttributeWidget(staticAttributesGroup, field, null, this, true, false);
			return staticAttributesGroup;
		}
		else
			return null;
	}

	private Composite createConstructorsGroup(Composite parent, final Class<?> clazz) {
		List<Constructor<?>> constructors = ClassModel.getInspector().getVisibleConstructors(clazz);
		if(constructors.size() > 0) {
			Composite constructorsGroup = new Composite(parent, SWT.NONE);
			
			GridLayout layout = new GridLayout(2, false);			
			constructorsGroup.setLayout(layout);
			for(Constructor<?> constructor : constructors) {
				ConstructorWidget widget = new ConstructorWidget(constructorsGroup, clazz, constructor, this);
				constructorMap.put(constructor, widget);
			}
			return constructorsGroup;
		}
		else
			return null;
	}

	private Group createStaticMethodsGroup(Composite parent, final Class<?> clazz) {
		List<Method> methods = ClassModel.getInspector().getVisibleStaticMethods(clazz);

		if(methods.size() > 0) {
			Group staticMethodsGroup = new Group(parent, SWT.NONE);
			staticMethodsGroup.setText(UIText.STATIC_METHODS.get());
			staticMethodsGroup.setLayout(new GridLayout(2, false));
			for(Method m : methods)	{
				MethodWidget widget = new MethodWidget(staticMethodsGroup, null, m, this);
				methodMap.put(m, widget);
			}
			return staticMethodsGroup;
		}
		else
			return null;
	}

	public void updateFields() {
		updateFields(null);
	}

	void updateConstructorArgs(Map<Constructor<?>, Object[]> constructors) {
		for(Constructor<?> c : constructorMap.keySet())
			if(constructors.containsKey(c))
				constructorMap.get(c).setArgs(constructors.get(c));
	}
	
	void updateMethodArgs(Map<Method, Object[]> methods) {
		for(Method m : methodMap.keySet())
			if(methods.containsKey(m))
				methodMap.get(m).setArgs(methods.get(m));
	}
}
