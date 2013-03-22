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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.AttributeWidget;
import pt.org.aguiaj.common.widgets.FieldContainer;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.MethodWidget;
import pt.org.aguiaj.common.widgets.TypeMemberMouseTrackAdapter;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.Highlightable;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.RemoveObjectCommand;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.standard.StandardNamePolicy;


public final class ObjectWidget extends FieldContainer implements Highlightable {

//	private final class TypeMemberMouseTrackAdapter extends MouseTrackAdapter {
//		private List<Method> methods;
//
//		private TypeMemberMouseTrackAdapter(Class<?> type) {
//			methods = ClassModel.getInspector().methodsOfSupertype(objectClass, type);
//		}
//
//		@Override
//		public void mouseEnter(MouseEvent e) {
//			for(Method m : methods)
//				highlight(m);
//		}
//
//		@Override
//		public void mouseExit(MouseEvent e) {
//			for(Method m : methods)
//				unhighlight(m);
//		}
//	}



	public enum Section {
		VISUAL, INTERNALS, ATTRIBUTES, PROPERTIES, OPERATIONS;
	}

	private final Object object;
	private final Class<?> objectClass;

	private final boolean hasExtension;
	private List<TypeWidget> extensions;

	private Stack<Composite> sections;
	private EnumMap<Section, Composite> sectionMap;
	private Map<Method, Highlightable> methodToWidget;
	private Menu menu;

	private static int PADDING = 0;


	public ObjectWidget(final Composite parent, final Object object) {		
		super(parent, SWT.BORDER);

		assert parent != null;
		assert object != null;

		this.object = object;
		this.objectClass = object.getClass();

		methodToWidget = new HashMap<Method, Highlightable>();

		setLayout(new FormLayout());

		menu = createMenu(object);
		setMenu(menu);

		sections = new Stack<Composite>();
		sectionMap = new EnumMap<ObjectWidget.Section, Composite>(Section.class);

		createHeader(menu);

		hasExtension = WidgetFactory.INSTANCE.hasExtension(objectClass);

		if(hasExtension) {
			Composite visualSection = createSection();
			visualSection.setLayout(new RowLayout(SWT.HORIZONTAL));

			this.extensions = WidgetFactory.INSTANCE.createWidgets(
					visualSection, 
					objectClass, 
					EnumSet.of(WidgetProperty.OBJECT_WIDGET));

			sectionMap.put(Section.VISUAL, visualSection);
		}					

		createPrivateAttributesGroup();
		createAttributesGroup();
		createPropertiesGroup();
		createCommandMethodsGroup();

		for(Composite section : sections)
			section.setMenu(menu);

		setBackground(AguiaJColor.OBJECT.getColor());
		for(Composite section : sections) {
			if(!section.equals(section(Section.INTERNALS)))
				SWTUtils.setColorRecursively(section, AguiaJColor.OBJECT.getColor());
		}

		show(Section.INTERNALS, false);
		show(Section.OPERATIONS, false);

		if(hasExtension) {
			show(Section.ATTRIBUTES, false);
			show(Section.PROPERTIES, false);
		}

		updateFields();

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				getParent().layout();
			}
		});
	}

	public boolean isVisible(Section sec) {
		return existsSection(sec) && section(sec).isVisible();
	}

	public Object getObject() {
		return object;
	}
	
	//	public String toString() {
	//		return object.toString();
	//	}

	public void die() {
		setEnabled(false);
	}

	public void highlight() {
		if(!isDisposed()) {
			setBackground(AguiaJColor.HIGHLIGHT.getColor());
			for(Composite section : sections) {
				if(!section.equals(section(Section.INTERNALS)))
					SWTUtils.setColorRecursively(section, AguiaJColor.HIGHLIGHT.getColor());
			}	
		}
	}

	public void highlight(Method method) {
		if(methodToWidget.containsKey(method))
			methodToWidget.get(method).highlight();
	}


	public void unhighlight() {
		if(!isDisposed()) {
			setBackground(AguiaJColor.OBJECT.getColor());
			for(Composite section : sections) {
				if(!section.equals(section(Section.INTERNALS)))
					SWTUtils.setColorRecursively(section, AguiaJColor.OBJECT.getColor());
			}		
		}
	}

	public void unhighlight(Method method) {
		if(methodToWidget.containsKey(method))
			methodToWidget.get(method).unhighlight();
	}


	private void createPrivateAttributesGroup() {
		List<Field> invisibleAttributes = ClassModel.getInstance().getInvisibleAttributes(objectClass);

		if(invisibleAttributes.size() > 0) {
			Composite privateAttributesGroup = createSection();
			sectionMap.put(Section.INTERNALS, privateAttributesGroup);

			RowLayout layout = new RowLayout(SWT.VERTICAL);
			layout.spacing = PADDING;
			privateAttributesGroup.setLayout(layout);

			ReverseIterator<Field> it = new ReverseIterator<Field>(invisibleAttributes);

			Class<?> owner = null;

			while(it.hasNext())	 {
				Field field = it.next();
				if(owner == null)
					owner = field.getDeclaringClass();

				if(!field.getDeclaringClass().equals(owner)) {
					new Label(privateAttributesGroup, SWT.SEPARATOR | SWT.HORIZONTAL);
					owner = field.getDeclaringClass();
				}
				new AttributeWidget(privateAttributesGroup, field, object, this, false, true);
			}
			SWTUtils.setColorRecursively(privateAttributesGroup, AguiaJColor.PRIVATES.getColor());

			createShowHide(UIText.SHOW_PRIVATE_ATTRIBUTES, UIText.HIDE_PRIVATE_ATTRIBUTES, privateAttributesGroup, Section.INTERNALS);
		}
	}

	static class ReverseIterator<E> implements Iterator<E> {
		private List<E> collection;
		private int next;		

		public ReverseIterator(List<E> list) {
			this.collection = list;
			next = list.size();
		}

		@Override
		public boolean hasNext() {
			return next != 0;
		}

		@Override
		public E next() {
			return collection.get(--next);
		}

		@Override
		public void remove() {
			collection.remove(next);
			if(next > 0)
				next--;
		}
	}


	private void createAttributesGroup() {
		List<Field> visibleAttributes = ClassModel.getInstance().getVisibleAttributes(objectClass);
		if(visibleAttributes.size() > 0) {
			Composite attributesGroup = createSection(); 
			sectionMap.put(Section.ATTRIBUTES, attributesGroup);

			RowLayout layout = new RowLayout(SWT.VERTICAL);
			layout.spacing = PADDING;
			attributesGroup.setLayout(layout);

			for(Field field : visibleAttributes)	
				new AttributeWidget(attributesGroup, field, object, this, true, false);	

			createShowHide(UIText.SHOW_ATTRIBUTES, UIText.HIDE_ATTRIBUTES, attributesGroup, Section.ATTRIBUTES);
		}
	}

	private void createPropertiesGroup() {
		List<Method> queryMethods = ClassModel.getInstance().getAccessorMethods(objectClass);
		if(queryMethods.size() > 0) {
			Composite propertiesGroup = createSection();
			sectionMap.put(Section.PROPERTIES, propertiesGroup);

			propertiesGroup.setLayout(createGridLayout());

			for(final Method m : queryMethods) {
				PropertyWidget widget = new PropertyWidget(propertiesGroup, object, m, this);
				methodToWidget.put(m, widget);
			}

			createShowHide(UIText.SHOW_PROPERTIES, UIText.HIDE_PROPERTIES, propertiesGroup, Section.PROPERTIES);
		}
	}

	//	private void createCommandMethodsGroup() {
	//		Map<Class<?>,List<Method>> commandMethodsByType = 
	//				ClassModel.getInstance().getCommandMethodsByType(objectClass);
	//
	//		if(commandMethodsByType.size() > 0) {
	//			Composite operationsGroup = createSection();
	//			sectionMap.put(Section.OPERATIONS, operationsGroup);
	//
	//			operationsGroup.setLayout(createGridLayout());
	//
	//			for(Class<?> interfacce : commandMethodsByType.keySet()) {
	//				for(Method m : commandMethodsByType.get(interfacce)) {
	//					MethodWidget widget = new MethodWidget(operationsGroup, object, m, this);
	//					methodToWidget.put(m, widget);
	//				}
	//			}
	//
	//			createShowHide(UIText.SHOW_OPERATIONS, UIText.HIDE_OPERATIONS, operationsGroup, Section.OPERATIONS);
	//		}
	//	}

	private void createCommandMethodsGroup() {
		List<Method> methods = ClassModel.getInstance().getCommandMethods(objectClass);

		if(methods.size() > 0) {
			Composite operationsGroup = createSection();
			sectionMap.put(Section.OPERATIONS, operationsGroup);

			operationsGroup.setLayout(createGridLayout());

			for(Method m : methods) {
				MethodWidget widget = new MethodWidget(operationsGroup, object, m, this);
				methodToWidget.put(m, widget);
			}

			createShowHide(UIText.SHOW_OPERATIONS, UIText.HIDE_OPERATIONS, operationsGroup, Section.OPERATIONS);
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

		nameLabel.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if(existsSection(Section.PROPERTIES) && isVisible(Section.OPERATIONS)) {
					show(Section.ATTRIBUTES, !hasExtension);
					show(Section.PROPERTIES, !hasExtension);
					show(Section.OPERATIONS, false);
				}
				else {
					show(Section.PROPERTIES, true);
					show(Section.OPERATIONS, true);
				}
			}
		});

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
				icon.addMouseTrackListener(new TypeMemberMouseTrackAdapter(this, type));
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

	public void show(Section sec, boolean state) {
		if(existsSection(sec)) {
			Composite section = section(sec);
			setVisible(section, state);
			if(showItemsTable.containsKey(section))
				showItemsTable.get(section).setEnabled(!state);

			if(hideItemsTable.containsKey(section))
				hideItemsTable.get(section).setEnabled(state);
		}
	}

	public void expand(EnumSet<Section> set) {
		for(Section sec : set)
			show(sec, true);

	}

	private Composite section(Section sec) {
		return sectionMap.get(sec);
	}

	private boolean existsSection(Section sec) {
		return sectionMap.containsKey(sec);
	}

	public Collection<Section> getExpandedSections() {
		EnumSet<Section> set = EnumSet.noneOf(Section.class);
		for(Section sec : Section.values())
			if(isVisible(sec))
				set.add(sec);

		return set;
	}	


	private MenuItem createMenuItem(String text) {
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(text);
		return item;
	}

	private Map<Composite,MenuItem> showItemsTable = newHashMap();
	private Map<Composite,MenuItem> hideItemsTable = newHashMap();

	private void createShowHide(UIText showText, UIText hideText, final Composite section, final Section sec) {
		addMenuSeparator();
		MenuItem showItem = createMenuItem(showText.get());
		showItem.setEnabled(false);
		showItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				show(sec, true);
			}
		});
		showItemsTable.put(section, showItem);

		MenuItem hideItem = createMenuItem(hideText.get());
		hideItem.setEnabled(!false);
		hideItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				show(sec, false);
			}
		});
		hideItemsTable.put(section, hideItem);
	}


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



	public void updateFields() {
		super.updateFields(object);

		//		verifyInvariant();

		try {
			if(extensions != null)
				for(TypeWidget extension : extensions)
					extension.update(object);
		}
		catch(Exception e) {
			AguiaJActivator.handlePluginError(e.getMessage());
			e.printStackTrace();
		}

		if(isDirty()) {								
			if(existsSection(Section.VISUAL))
				section(Section.VISUAL).layout();
			setUpdated();
		}

		layout();
		pack();	
	}




}
