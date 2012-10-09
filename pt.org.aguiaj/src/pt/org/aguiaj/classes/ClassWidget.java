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
package pt.org.aguiaj.classes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.common.widgets.AttributeWidget;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.common.widgets.MethodWidget;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.InspectionPolicy;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.standard.StandardNamePolicy;


public class ClassWidget extends FieldContainer {

	private List<Field> staticFields;
	private List<Constructor<?>> constructors;

	private Inspector inspector;
	
	public ClassWidget(Composite parent, final Class<?> clazz) {
		super(parent, SWT.BORDER);
		
		inspector = ClassModel.getInstance().getInspector();
		constructors = inspector.getVisibleConstructors(clazz);

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
		
		DocumentationView.getInstance().addDocumentationSupport(classNameLabel.getControl(), clazz);
		
		if(inspector.isStaticClass(clazz))
			new LabelWidget.Builder()
				.text("(static)")
				.small()
				.create(classHeader);
				
		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, 5);
		labelData.left = new FormAttachment(0, 5);
		labelData.right = new FormAttachment(100, -5);
		classHeader.setLayoutData(labelData);
		
		Field[] fields = clazz.getDeclaredFields();
		staticFields = new ArrayList<Field>();

		for(Field field : fields)
			if(inspector.getPolicy().isStaticFieldVisible(field) && !field.isEnumConstant()) {
				field.setAccessible(true);
				staticFields.add(field);
			}
			
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
		
//		ClassModel.getInstance().addClass(clazz);
	}

	private Group createConstantsGroup(Composite parent, final Class<?> clazz) {
		Group constantsGroup = new Group(parent, SWT.NONE);
		constantsGroup.setText("Enum constants");
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
				
				DocumentationView.getInstance().addDocumentationSupport(label.getControl(), field);
				
				label.addHyperlinkAction(new Listener () {
					public void handleEvent(Event event) {
						try {
							Object enumConst = field.get(null);
							String source = clazz.getSimpleName() + "." + field.getName();
							String ref = ObjectModel.aspectOf().nextReference(field.getType());
							new NewReferenceCommand(clazz, enumConst, source, ref).execute();
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
			staticAttributesGroup.setText("Static attributes");
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
		if(constructors.size() > 0) {
			Composite constructorsGroup = new Composite(parent, SWT.NONE);
			
			GridLayout layout = new GridLayout(2, false);			
			constructorsGroup.setLayout(layout);
			for(Constructor<?> constructor : constructors)
				new ConstructorWidget(constructorsGroup, clazz, constructor, this);
			return constructorsGroup;
		}
		else
			return null;
	}

	private Group createStaticMethodsGroup(Composite parent, final Class<?> clazz) {
		List<Method> methods = inspector.getVisibleStaticMethods(clazz);

		if(methods.size() > 0) {
			Group staticOperationsGroup = new Group(parent, SWT.NONE);
			staticOperationsGroup.setText("Static operations");
			staticOperationsGroup.setLayout(new GridLayout(2, false));
			for(Method m : methods)	
				new MethodWidget(staticOperationsGroup, clazz, null, m, this);
			return staticOperationsGroup;
		}
		else
			return null;
	}

	public void updateFields() {
		updateFields(null);
	}

	
	
}
