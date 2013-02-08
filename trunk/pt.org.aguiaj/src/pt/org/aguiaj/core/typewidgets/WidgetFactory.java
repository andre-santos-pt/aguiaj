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
import pt.org.aguiaj.extensibility.CanvasVisualizationWidget;
import pt.org.aguiaj.extensibility.VisualizationWidget;
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





	private List<Constructor<? extends VisualizationWidget<?>>> compatibleExtensions(Class<?> clazz) {
		List<Constructor<? extends VisualizationWidget<?>>> list = new ArrayList<>();

		for(Class<?> key : objectWidgetTypeTable.keySet()) {
			if(key.isAssignableFrom(clazz))
				list.addAll(objectWidgetTypeTable.get(key));
		}

		return list;
	}


	private Constructor<? extends VisualizationWidget<?>> bestExtension(Class<?> clazz) {
		Collection<Constructor<? extends VisualizationWidget<?>>> all = 
				objectWidgetTypeTable.get(clazz);

		if(!all.isEmpty()) {
			return all.iterator().next();
		}
		else {
			for(Class<?> key : objectWidgetTypeTable.keySet()) {
				if(key.isAssignableFrom(clazz))
					return bestExtension(key);
			}
		}

		return ReflectionUtils.declaresToString(clazz) ? stringWidget : null;
	}

//	public TypeWidget createWidget(Composite parent, 
//			Class<?> clazz,
//			Set<WidgetProperty> properties) {
//
//		return createWidgets(parent, clazz, properties).get(0);
//	}

	public List<TypeWidget> createWidgets(Composite parent, 
			Class<?> clazz,
			Set<WidgetProperty> properties) {

		assert parent != null;
		assert clazz != null;
		assert WidgetProperty.isValidSet(properties);

		List<TypeWidget> widgets = new ArrayList<>(1);

		//		TypeWidget widget = null;

		WidgetProperty ownerType = WidgetProperty.getOwnerType(properties);

		// primitive types
		if(clazz.isPrimitive()) { 

			if(widgetTypeTable.containsKey(clazz)) { 
				Constructor<? extends TypeWidget> constructor = widgetTypeTable.get(clazz);
				try {
					widgets.add(constructor.newInstance(
							parent, 
							ownerType,
							properties.contains(WidgetProperty.MODIFIABLE)));
				}
				catch (Exception e) {
					System.err.println("Error creating plugglable type widget " + 
							constructor.getDeclaringClass().getName());
					e.printStackTrace();
					widgets.add(new NotSupportedWidget(parent, ownerType, clazz));
				}
			}
			else {
				widgets.add(new NotSupportedWidget(parent, ownerType, clazz));
			}
		}

		// objects
		else {

			if(properties.contains(WidgetProperty.MODIFIABLE) && 
					ownerType != WidgetProperty.PROPERTY) {

				widgets.add(new SelectReferenceWidget(parent, clazz, ownerType));	
			}
			else if(clazz.isArray() &&					
					!clazz.getComponentType().isArray() &&
					ownerType.equals(WidgetProperty.PROPERTY)) {

				if(clazz.getComponentType().isPrimitive()) {
					widgets.add(new ExtensionTypeWidget(parent, ownerType,  new StringObjectWidget()));
				}
				else {
					widgets.add(new ExtensionTypeWidget(parent, ownerType,  new TableArrayObjectWidget()));
				}
			}
			else if(!properties.contains(WidgetProperty.NO_EXTENSION)) {

					
				for(Constructor<? extends VisualizationWidget<?>> constructor : compatibleExtensions(clazz)) {
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
					}
					else {
						VisualizationWidget<?> extension = null;
						try {
							extension = constructor.newInstance();
						} catch (Exception e) {
							e.printStackTrace();
						} 
						widgets.add(new ExtensionTypeWidget(parent, ownerType, extension));
					}
				}
			}
		}

		if(widgets.isEmpty())
			widgets.add(new ExtensionTypeWidget(parent, ownerType,  new StringObjectWidget()));

		for(TypeWidget w : widgets)
			SWTUtils.setTooltipRecursively(w.getControl(), clazz.getSimpleName());

		return widgets;
	}
}
