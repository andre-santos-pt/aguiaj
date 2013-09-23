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
package pt.org.aguiaj.core.typewidgets;

import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import pt.org.aguiaj.extensibility.canvas.CanvasVisualizationWidget;
import pt.org.aguiaj.standard.extensions.ArrayObjectWidget;
import pt.org.aguiaj.standard.extensions.StringObjectWidget;
import pt.org.aguiaj.standard.extensions.TableArrayObjectWidget;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public enum WidgetFactory {
	INSTANCE;

	private static Constructor<? extends VisualizationWidget<?>> stringWidget;

	static {
		try {
			stringWidget = StringObjectWidget.class.getConstructor();
		} catch (Exception e) {		
			e.printStackTrace();
		}
	}


	private Map<Class<?>, Constructor<? extends TypeWidget>> widgetTypeTable;
	private Multimap<Class<?>, Constructor<? extends VisualizationWidget<?>>> objectWidgetTypeTable;

	private WidgetFactory() {
		widgetTypeTable = newHashMap();
		objectWidgetTypeTable = LinkedListMultimap.create();
	}


	public void addWidgetType(Class<?> clazz, Class<? extends TypeWidget> widgetType) {
		Constructor<? extends TypeWidget> constructor = null;
		try {
			constructor = widgetType.getConstructor(Composite.class, WidgetProperty.class, boolean.class);
		}
		catch (NoSuchMethodException e) {
			System.err.println("Object Widget " + widgetType.getName() + 
					" should have a constructor with arguments (" + Composite.class.getName() + 
					", " + WidgetProperty.class.getName() + ", " + boolean.class.getName() + ")");
		}
		if(constructor != null)
			widgetTypeTable.put(clazz, constructor);		
	}



	public void addVisualizationWidgetType(Class<?>[] classes, Class<? extends VisualizationWidget<?>> objectWidgetType) throws Exception {
		Constructor<? extends VisualizationWidget<?>> constructor = null;

		try {
			constructor = objectWidgetType.getConstructor();
		}
		catch (NoSuchMethodException e) {
			throw new Exception("Pluggable Object Widget " + objectWidgetType.getName() + 
					" should have a public parameterless constructor.");
		}
		if(constructor != null)
			for(Class<?> clazz : classes)
				objectWidgetTypeTable.put(clazz, constructor);		
	}


	public boolean hasDirectExtension(Class<?> clazz) {
		return !objectWidgetTypeTable.get(clazz).isEmpty();
	}

	public boolean hasExtension(Class<?> clazz) {
		return bestExtension(clazz) != null;
	}





	private List<Constructor<? extends VisualizationWidget<?>>> compatibleExtensions(Class<?> clazz, WidgetProperty ownerType) {
		List<Constructor<? extends VisualizationWidget<?>>> list = new ArrayList<Constructor<? extends VisualizationWidget<?>>>();

		for(Class<?> key : objectWidgetTypeTable.keySet()) {
			if(compatible(key, clazz))
				for(Constructor<? extends VisualizationWidget<?>> c : objectWidgetTypeTable.get(key)) {
					if(c.getDeclaringClass().equals(ArrayObjectWidget.class) && ownerType == WidgetProperty.PROPERTY)
						continue;
					else
						list.add(c);
				}
		}

		return list;
	}
	
	private boolean compatible(Class<?> a, Class<?> b) {
		if(a.isArray() && b.isArray())
			return ReflectionUtils.equalArrayDim(a, b) && a.isAssignableFrom(b);
		else
			return a.isAssignableFrom(b);
	}

	

	private Constructor<? extends VisualizationWidget<?>> bestExtension(Class<?> clazz) {
		Collection<Constructor<? extends VisualizationWidget<?>>> all = 
				objectWidgetTypeTable.get(clazz);

		if(!all.isEmpty()) {
			return all.iterator().next();
		}
		else {
			for(Class<?> key : objectWidgetTypeTable.keySet()) {
				if(!key.equals(Object.class) && key.isAssignableFrom(clazz))
					return bestExtension(key);
			}
		}

		return ReflectionUtils.declaresToString(clazz) ? stringWidget : null;
	}

	public TypeWidget createWidget(Composite parent, 
			Class<?> clazz,
			Set<WidgetProperty> properties) {

		return createWidgets(parent, clazz, properties, true).get(0);
	}

	public List<TypeWidget> createWidgets(Composite parent, 
			Class<?> type,
			Set<WidgetProperty> properties) {
		
		return createWidgets(parent, type, properties, false);
	}
	
	private List<TypeWidget> createWidgets(Composite parent, 
			Class<?> type,
			Set<WidgetProperty> properties, boolean single) {

		assert parent != null;
		assert type != null;
		assert WidgetProperty.isValidSet(properties);

		List<TypeWidget> widgets = new ArrayList<TypeWidget>(single ? 1 : 3);
		
		WidgetProperty ownerType = WidgetProperty.getOwnerType(properties);

		if(type.isPrimitive()) {
			widgets.add(createPrimitiveTypeWidget(parent, type, properties, ownerType));
		}
		else {
			if(properties.contains(WidgetProperty.MODIFIABLE) && ownerType != WidgetProperty.PROPERTY) {
				widgets.add(new SelectReferenceWidget(parent, type, ownerType));	
			}
			else if(type.isArray() && !type.getComponentType().isArray()) {
//					ownerType.equals(WidgetProperty.PROPERTY)) {

				if(type.getComponentType().isPrimitive() && ownerType.equals(WidgetProperty.ARRAYPOSITION)) {
					widgets.add(new ExtensionTypeWidget(parent, type, ownerType,  new StringObjectWidget()));
				}
//				else if(ownerType.equals(WidgetProperty.PROPERTY)) {
//					widgets.add(new ExtensionTypeWidget(parent, type, ownerType,  new TableArrayObjectWidget()));
//				}
			}
		}

		if(!properties.contains(WidgetProperty.NO_EXTENSION) && ownerType != WidgetProperty.PARAMETER) {
			addExtensionWidgets(parent, type, widgets, ownerType, single);	
		}
		
		if(widgets.isEmpty())
			widgets.add(new ExtensionTypeWidget(parent, type, ownerType, new StringObjectWidget()));

		for(TypeWidget w : widgets)
			SWTUtils.setTooltipRecursively(w.getControl(), type.getSimpleName());

		return widgets;
	}


	private void addExtensionWidgets(Composite parent, Class<?> type,
			List<TypeWidget> widgets, WidgetProperty ownerType, boolean single) {
		
		for(Constructor<? extends VisualizationWidget<?>> constructor : compatibleExtensions(type, ownerType)) {
			
			if(CanvasVisualizationWidget.class.isAssignableFrom(constructor.getDeclaringClass()) && 
					ownerType != WidgetProperty.PARAMETER) {

				CanvasVisualizationWidget extension = null;
				try {
					extension = (CanvasVisualizationWidget) constructor.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				} 
				CanvasObjectWidgetExtension widget = new CanvasObjectWidgetExtension(parent, extension, ownerType);
				widget.initialize();
				widgets.add(widget);
				if(single)
					return;
			}
			else if(VisualizationWidget.class.isAssignableFrom(constructor.getDeclaringClass())) {
				VisualizationWidget<?> extension = null;
				try {
					extension = constructor.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(extension.include(type))
					widgets.add(new ExtensionTypeWidget(parent, type, ownerType, extension));
				
				if(single)
					return;
			}
		}
	}


	private TypeWidget createPrimitiveTypeWidget(Composite parent, Class<?> type,
			Set<WidgetProperty> properties, WidgetProperty ownerType) {
		
		if(!type.isPrimitive())
			throw new IllegalArgumentException("Type is not primitive");
		
		if(widgetTypeTable.containsKey(type)) { 
			Constructor<? extends TypeWidget> constructor = widgetTypeTable.get(type);
			try {
				return constructor.newInstance(
						parent, 
						ownerType,
						properties.contains(WidgetProperty.MODIFIABLE));
			}
			catch (Exception e) {
				System.err.println("Error creating plugglable type widget " + 
						constructor.getDeclaringClass().getName());
				e.printStackTrace();
				return new NotSupportedWidget(parent, ownerType, type);
			}
		}
		else {
			return new NotSupportedWidget(parent, ownerType, type);
		}
	}
}
