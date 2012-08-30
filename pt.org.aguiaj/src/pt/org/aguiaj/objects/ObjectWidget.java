/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package pt.org.aguiaj.objects;

import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.Highlightable;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.AttributeWidget;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.MethodWidget;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.RemoveObjectCommand;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.standard.StandardNamePolicy;

import com.google.common.collect.Sets;

// TODO fail-proof extensions

public final class ObjectWidget extends FieldContainer implements Highlightable {

	private final Object object;
	private final Class<?> objectClass;

	private TypeWidget extension;
	
	private Stack<Composite> sections;
	private Composite visualSection;
	private Composite privateAttributesGroup;
	private Composite attributesGroup;
	private Composite propertiesGroup;
	private Composite operationsGroup;

	private Menu menu;

	private static int PADDING = 0;

	public ObjectWidget(
			final Composite parent,
			final Object object) {		
		
		super(parent, SWT.BORDER);

		assert parent != null;
		assert object != null;

		this.object = object;
		this.objectClass = object.getClass();
		
		setLayout(new FormLayout());

		menu = createMenu(object);
		setMenu(menu);

		sections = new Stack<Composite>();

		createHeader(menu);

		boolean hasExtension = WidgetFactory.INSTANCE.hasExtension(objectClass);
		
		if(hasExtension) {
			visualSection = createSection();
			this.extension = WidgetFactory.INSTANCE.createWidget(
					visualSection, 
					objectClass, 
					EnumSet.of(WidgetProperty.OBJECT_WIDGET));					
		}					

		createPrivateAttributesGroup();
		createAttributesGroup();
		createPropertiesGroup();
		createCommandMethodsGroup();

		for(Composite section : sections)
			section.setMenu(menu);

		setBackground(AguiaJColor.OBJECT.getColor());
		for(Composite section : sections) {
			if(!section.equals(privateAttributesGroup))
				SWTUtils.setColorRecursively(section, AguiaJColor.OBJECT.getColor());
		}

		showPrivateAttributes(false);
		showOperations(false);

		if(hasExtension) {
			showAttributes(false);
			showProperties(false);
		}
			
		updateFields();

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				getParent().layout();
			}
		});
	}


	public boolean isOperationsVisible() {
		return operationsGroup != null && operationsGroup.isVisible();
	}

	public boolean isPrivateAttributesVisible() {
		return privateAttributesGroup != null && privateAttributesGroup.isVisible();
	}

	public boolean isPropertiesVisible() {
		return propertiesGroup != null && propertiesGroup.isVisible();
	}
	
	public String toString() {
		return object.toString();
	}

	public void die() {
		setEnabled(false);
		SWTUtils.setColorRecursively(this, AguiaJColor.DEAD_OBJECT.getColor());
	}

	public void highlight() {
		if(!isDisposed()) {
			setBackground(AguiaJColor.HIGHLIGHT.getColor());
			for(Composite section : sections) {
				if(!section.equals(privateAttributesGroup))
					SWTUtils.setColorRecursively(section, AguiaJColor.HIGHLIGHT.getColor());
			}	
		}
	}

	public void unhighlight() {
		if(!isDisposed()) {
			setBackground(AguiaJColor.OBJECT.getColor());
			for(Composite section : sections) {
				if(!section.equals(privateAttributesGroup))
					SWTUtils.setColorRecursively(section, AguiaJColor.OBJECT.getColor());
			}		
		}
	}




	private void createPrivateAttributesGroup() {
		List<Field> invisibleAttributes = ClassModel.getInstance().getInvisibleAttributes(objectClass);

		if(invisibleAttributes.size() > 0) {
			privateAttributesGroup = createSection();
			RowLayout layout = new RowLayout(SWT.VERTICAL);
			layout.spacing = PADDING;
			privateAttributesGroup.setLayout(layout);

			for(Field field : invisibleAttributes)	
				new AttributeWidget(privateAttributesGroup, field, object, this, false, true);

			SWTUtils.setColorRecursively(privateAttributesGroup, AguiaJColor.PRIVATES.getColor());

			createShowHide(UIText.SHOW_PRIVATE_ATTRIBUTES, UIText.HIDE_PRIVATE_ATTRIBUTES, privateAttributesGroup);
		}
	}

	private void createAttributesGroup() {
		List<Field> visibleAttributes = ClassModel.getInstance().getVisibleAttributes(objectClass);
		if(visibleAttributes.size() > 0) {
			attributesGroup = createSection(); 
			RowLayout layout = new RowLayout(SWT.VERTICAL);
			layout.spacing = PADDING;
			attributesGroup.setLayout(layout);

			for(Field field : visibleAttributes)	
				new AttributeWidget(attributesGroup, field, object, this, true, false);	
			
			createShowHide(UIText.SHOW_ATTRIBUTES, UIText.HIDE_ATTRIBUTES, attributesGroup);
		}
	}

	private void createPropertiesGroup() {
		List<Method> queryMethods = ClassModel.getInstance().getAccessorMethods(objectClass);
		if(queryMethods.size() > 0) {
			propertiesGroup = createSection();
			propertiesGroup.setLayout(createGridLayout());

			for(final Method propertyMethod : queryMethods) {				
				new PropertyWidget(propertiesGroup, object, propertyMethod, this);
			}
			
			createShowHide(UIText.SHOW_PROPERTIES, UIText.HIDE_PROPERTIES, propertiesGroup);
		}
	}

	private void createCommandMethodsGroup() {
		Map<Class<?>,List<Method>> commandMethodsByType = 
			ClassModel.getInstance().getCommandMethodsByType(objectClass);

		if(commandMethodsByType.size() > 0) {
			operationsGroup = createSection();
			operationsGroup.setLayout(createGridLayout());

			for(Class<?> interfacce : commandMethodsByType.keySet()) {
				for(Method method : commandMethodsByType.get(interfacce)) {
					new MethodWidget(operationsGroup, objectClass, getObject(), method, this);
				}
			}

			createShowHide(UIText.SHOW_OPERATIONS, UIText.HIDE_OPERATIONS, operationsGroup);
		}
	}

	private GridLayout createGridLayout() {
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = PADDING;
		layout.verticalSpacing = PADDING;
		layout.marginBottom = PADDING;
		layout.marginTop = PADDING;
		layout.marginLeft = PADDING;
		layout.marginRight = PADDING;
		return layout;
	}


	private String headerText() {
		String className = objectClass.isAnonymousClass() && objectClass.getSuperclass().isEnum() ?
			StandardNamePolicy.prettyClassName(objectClass.getSuperclass()) :
			StandardNamePolicy.prettyClassName(objectClass);
		
		if(ClassModel.getInstance().ambiguousClassName(objectClass)) 
			className = objectClass.getName();

		return ": " + className;
	}

	private Composite createHeader(Menu menu) {
		Composite classHeader = createSection();
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.spacing = 5;
		classHeader.setLayout(layout);

		LabelWidget nameLabel = new LabelWidget.Builder()
		.text(headerText())
		.big()
		.toolTip(UIText.OBJECT_OF_TYPE.get(objectClass.getSimpleName()))
		.create(classHeader);

		nameLabel.getControl().setMenu(menu);

		List<Class<?>> types = Inspector.getAllCompatibleTypes(objectClass);

		for(final Class<?> type : types) {
			if(ClassModel.getInstance().isClassInUse(type) && !type.equals(Enum.class) && !type.isEnum()) {
				IconWidget icon = new IconWidget(classHeader, type);
				String toolTip = type.isInterface() ?
						type.getSimpleName() + " (interface)" :
						UIText.IS_A.get(objectClass.getSimpleName(), type.getSimpleName());
						
					
				if(!type.isInterface()) {
					toolTip += " (";
					if(Modifier.isAbstract(type.getModifiers()))
						toolTip += "abstract ";
					
					toolTip += "class)";
				}
				
				icon.setToolTipText(toolTip);
			}
		}

		FormData labelData = new FormData();
		labelData.top = new FormAttachment(0, PADDING);
		labelData.left = new FormAttachment(0, PADDING);
		labelData.right = new FormAttachment(100, -PADDING);
		classHeader.setLayoutData(labelData);

		classHeader.setMenu(menu);

		return classHeader;
	}


	private Menu createMenu(final Object object) {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem removeItem = new MenuItem(menu, SWT.PUSH);
		removeItem.setText(UIText.REMOVE.get());
		removeItem.setImage(AguiaJImage.DELETE.getImage());
		removeItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				new RemoveObjectCommand(object).execute();
			}
		});		
		return menu;
	}

	private void addMenuSeparator() {
		new MenuItem(menu, SWT.SEPARATOR);
	}

	public enum ObjectSection {
		PRIVATE_ATTRIBUTES, ATTRIBUTES, PROPERTIES, OPERATIONS;
	}
	
	public void showPrivateAttributes(boolean state) {
		show(privateAttributesGroup, state);
	}

	public void showAttributes(boolean state) {
		show(attributesGroup, state);
	}
	
	public void showProperties(boolean state) {
		show(propertiesGroup, state);
	}


	public void showOperations(boolean state) {
		show(operationsGroup, state);
	}

	private void show(Composite section, boolean state) {
		if(section != null && visibleSections.contains(section)) {
			setVisible(section, state);
			showItemsTable.get(section).setEnabled(!state);
			hideItemsTable.get(section).setEnabled(state);
		}
	}



	private MenuItem createMenuItem(String text) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(text);
		return item;
	}

	private Set<Composite> visibleSections = Sets.newHashSet();
	private Map<Composite,MenuItem> showItemsTable = newHashMap();
	private Map<Composite,MenuItem> hideItemsTable = newHashMap();

	private void createShowHide(UIText showText, UIText hideText, final Composite section) {
		visibleSections.add(section);
		addMenuSeparator();
		MenuItem showItem = createMenuItem(showText.get());
		showItem.setEnabled(false);
		showItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				show(section, true);
			}
		});
		showItemsTable.put(section, showItem);

		MenuItem hideItem = createMenuItem(hideText.get());
		hideItem.setEnabled(!false);
		hideItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				show(section, false);
			}
		});
		hideItemsTable.put(section, hideItem);
	}



	//	private void createShowHideOperations() {
	//
	//		showOperationsItem = new MenuItem(menu, SWT.PUSH);
	//		showOperationsItem.setText(UIText.SHOW_OPERATIONS.get());
	//		showOperationsItem.setEnabled(operationsExist);
	//		showOperationsItem.addListener(SWT.Selection, new Listener() {
	//			public void handleEvent(Event e) {
	//				showOperations(true);
	//			}
	//		});
	//
	//		hideOperationsItem = new MenuItem(menu, SWT.PUSH);
	//		hideOperationsItem.setText(UIText.HIDE_OPERATIONS.get());
	//		hideOperationsItem.setEnabled(operationsExist);
	//		hideOperationsItem.addListener(SWT.Selection, new Listener() {
	//			public void handleEvent(Event e) {
	//				showOperations(false);
	//			}
	//		});
	//	}

	private Composite createSection() {
		Composite section = new Composite(this, SWT.NONE);
		FormData groupData = new FormData();

		if(sections.isEmpty()) {
			groupData.top = new FormAttachment(0, PADDING);	
		}
		else {
			groupData.top = new FormAttachment(sections.peek(), PADDING);			
		}

		groupData.left = new FormAttachment(0, PADDING);
		groupData.right = new FormAttachment(100, -PADDING);

		section.setLayoutData(groupData);
		sections.push(section);
		return section;
	}

	private void setVisible(Composite section, boolean visible) {
		for(Composite sec : sections) {
			if(sec.equals(section)) {
				sec.setVisible(visible);
				FormData formData = (FormData) sec.getLayoutData();
				formData.width = visible ? SWT.DEFAULT : 0;
				formData.height = visible ? SWT.DEFAULT : 0;
				break;
			}
		}

		layout();
		ObjectsView.getInstance().updateLayout(null);
	}

	public Object getObject() {
		return object;
	}


	public void updateFields() {
		super.updateFields(object);

		try {
			if(extension != null)
				extension.update(object);
		}
		catch(Exception e) {
			AguiaJActivator.handlePluginError(e.getMessage());
			e.printStackTrace();
		}

		if(isDirty()) {								
			if(visualSection != null)
				visualSection.layout();
			setUpdated();
		}

		//		}
		//		catch(Exception e) {
		//			String message = e.getMessage();
		//			MessageBox messageBox = new MessageBox(Display.getDefault().getActiveShell(), SWT.ERROR);
		//			messageBox.setText(StandardNamePolicy.prettyClassName(e.getClass()));
		//			messageBox.setMessage(message == null ? "" : message);
		//			messageBox.open();
		//		}
		layout();
		pack();				
	}	
}
