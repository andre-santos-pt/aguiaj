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
package pt.org.aguiaj.core.typewidgets;

import static com.google.common.collect.Maps.newHashMap;

import java.lang.reflect.Constructor;
import java.util.Collection;
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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public enum WidgetFactory {
	INSTANCE;

	private Map<Class<?>, Constructor<? extends TypeWidget>> widgetTypeTable;
	private Multimap<Class<?>, Constructor<? extends VisualizationWidget<?>>> objectWidgetTypeTable;

	private WidgetFactory() {
		widgetTypeTable = newHashMap();
		objectWidgetTypeTable = ArrayListMultimap.create();		
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

	//	private static int depth(Class<?> clazz) {
	//		if(clazz.equals(Object.class))
	//			return 1;
	//		else
	//			return 2;
	//	}

	
	private static Constructor<? extends VisualizationWidget<?>> stringWidget;
	
	static {
		try {
			stringWidget = StringObjectWidget.class.getConstructor();
		} catch (Exception e) {		
			e.printStackTrace();
		}
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
	

	public TypeWidget createWidget(Composite parent, 
			Class<?> clazz,
			Set<WidgetProperty> properties) {

		assert parent != null;
		assert clazz != null;
		assert WidgetProperty.isValidSet(properties);
		
		TypeWidget widget = null;

		WidgetProperty ownerType = WidgetProperty.getOwnerType(properties);
		
		// primitive types
		if(clazz.isPrimitive()) { 
			
			if(widgetTypeTable.containsKey(clazz)) { 
				Constructor<?> constructor = widgetTypeTable.get(clazz);
				try {
					widget = (TypeWidget) constructor.newInstance(
							parent, 
							ownerType,
							properties.contains(WidgetProperty.MODIFIABLE));
				}
				catch (Exception e) {
					System.err.println("Error creating plugglable type widget " + 
							constructor.getDeclaringClass().getName());
					e.printStackTrace();
					widget = new NotSupportedWidget(parent, ownerType, clazz);
				}
			}
			else {
				widget = new NotSupportedWidget(parent, ownerType, clazz);
			}
		}
		
		// objects
		else {
			
			if(properties.contains(WidgetProperty.MODIFIABLE) && 
				ownerType != WidgetProperty.PROPERTY) {
				
				widget = new SelectReferenceWidget(parent, clazz, ownerType);	
			}
			else if(clazz.isArray() &&					
					!clazz.getComponentType().isArray() &&
					ownerType.equals(WidgetProperty.PROPERTY)) {
					if(clazz.getComponentType().isPrimitive()) {
						widget = new ExtensionTypeWidget(parent, ownerType,  new StringObjectWidget());
					}
					else {
						widget = new ExtensionTypeWidget(parent, ownerType,  new TableArrayObjectWidget());
					}
			}
			else if(!properties.contains(WidgetProperty.NO_EXTENSION)) {

				Constructor<? extends VisualizationWidget<?>> constructor = bestExtension(clazz);

				if(constructor != null) {
					if(CanvasVisualizationWidget.class.isAssignableFrom(constructor.getDeclaringClass()) && 
							ownerType != WidgetProperty.PARAMETER) {

						CanvasVisualizationWidget extension = null;
						try {
							extension = (CanvasVisualizationWidget) constructor.newInstance();
						} catch (Exception e) {
							e.printStackTrace();
						} 
						widget = new CanvasObjectWidgetExtension(parent, extension, ownerType);
						((CanvasObjectWidgetExtension) widget).initialize();
					}
					else {
						VisualizationWidget<?> extension = null;
						try {
							extension = constructor.newInstance();
						} catch (Exception e) {
							e.printStackTrace();
						} 
						widget = new ExtensionTypeWidget(parent, ownerType, extension);
					}
				}
			}
		}

		if(widget == null)
			widget = new ExtensionTypeWidget(parent, ownerType,  new StringObjectWidget());

		assert widget != null;	
		SWTUtils.setTooltipRecursively(widget.getControl(), clazz.getSimpleName());
		return widget;
	}
}
