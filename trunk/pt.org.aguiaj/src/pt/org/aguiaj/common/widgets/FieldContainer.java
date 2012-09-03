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
package pt.org.aguiaj.common.widgets;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;


public class FieldContainer extends Composite {

	private static class ArrayPosition {
		Object arrayObject;
		Class<?> componentType;
		int position;
		TypeWidget widget;
		
		public ArrayPosition(Object arrayObject, Class<?> componentType, int position, TypeWidget widget) {
			this.arrayObject = arrayObject;
			this.componentType = componentType;
			this.position = position;
			this.widget = widget;
		}
	}

	private Map<AccessibleObject, TypeWidget> fieldTable;
	private Map<Integer, ArrayPosition> arrayPositionTable;

	private boolean isDirty;

	public FieldContainer(Composite parent, int style) {
		super(parent, style);
		fieldTable = new HashMap<AccessibleObject, TypeWidget>();
		arrayPositionTable = new LinkedHashMap<Integer, ArrayPosition>();
		isDirty = true;
	}

	protected boolean isDirty() {
		return isDirty;
	}

	protected void setUpdated() {
		isDirty = false;
	}

	public void mapToWidget(AccessibleObject ao, TypeWidget widget) {
		assert ao != null;
		assert widget != null;

		fieldTable.put(ao, widget);
	}

	public void mapArrayFieldToWidget(Object arrayObject, Class<?> componentType, int position, TypeWidget widget) {
		assert arrayObject != null && arrayObject.getClass().isArray();
		assert componentType != null;		

		arrayPositionTable.put(position, new ArrayPosition(arrayObject, componentType, position, widget));
	}

	public void updateFields(Object object) {
		if(object != null && object.getClass().isArray() && !object.getClass().getComponentType().isArray()) {

			for(Integer p : arrayPositionTable.keySet())
				updateArrayPosition(arrayPositionTable.get(p));
		}
		else {
			for(AccessibleObject ao : fieldTable.keySet()) {				
				if(ao instanceof Field) {
					if(!Modifier.isFinal(((Field) ao).getModifiers()))
						updateField((Field) ao, object);
				}
				else if(ao instanceof Method) {
					updateProperty((Method) ao, object);
				}
			}
		}		
		if(!isDisposed()) {
			layout();
			pack();
		}
	}		



	private void updateField(Field field, Object object) {
		TypeWidget fieldWidget = fieldTable.get(field);
		Object newVal = null;
		try {
			field.setAccessible(true);
			newVal = field.get(object);	
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		updateTypeWidget(fieldWidget, field.getType(), newVal);
	}

	private void updateProperty(Method method, Object object) {
		TypeWidget propWidget = fieldTable.get(method);
		Object newVal = null;
		MethodInvocationCommand command = new MethodInvocationCommand(object, "na", method, new Object[0], new String[0]);
		command.setSilent();
		command.execute();		
		command.waitToFinish();
		newVal = command.getResultingObject();
		updateTypeWidget(propWidget, method.getReturnType(), newVal);
	}

	private void updateArrayPosition(ArrayPosition arrayPosition) {
		Class<?> type = arrayPosition.componentType;
		Object newVal = null;
		if(type.equals(int.class)) {
			newVal = Array.getInt(arrayPosition.arrayObject, arrayPosition.position);
		}
		else if(type.equals(double.class)) {
			newVal = Array.getDouble(arrayPosition.arrayObject, arrayPosition.position);
		}
		else if(type.equals(boolean.class)) {
			newVal = Array.getBoolean(arrayPosition.arrayObject, arrayPosition.position);
		}
		else if(type.equals(char.class)) {
			newVal = Array.getChar(arrayPosition.arrayObject, arrayPosition.position);
		}
		else {
			newVal = Array.get(arrayPosition.arrayObject, arrayPosition.position);
		}
		updateTypeWidget(arrayPosition.widget, arrayPosition.componentType, newVal);

	}

	private void updateTypeWidget(TypeWidget widget, Class<?> type, Object newVal) {

		Object previous = widget.getObject();	

		if(needsUpdate(widget, type, newVal, previous)) {
			isDirty = true;
			widget.update(newVal);
			widget.highlight();			
		}
	}

	private boolean needsUpdate(TypeWidget widget, Class<?> type, Object newVal, Object previous) {
		boolean update = true;

		if(previous == null && newVal == null) {
			update = false;
		}
		else if(type.isArray()) { // && type.getComponentType().isArray()) {			
			if(widget != null) {
				String prevValText = widget.getTextualRepresentation();
				String newValText = ReflectionUtils.getTextualRepresentation(newVal, true);
				if(prevValText != null && prevValText.equals(newValText)) {
					update = false;
				}
			}
		}		
		else if(type.isArray() && !type.getComponentType().isPrimitive()) {
			update = !Arrays.deepEquals((Object[]) previous, (Object[]) newVal);
		}
		else if(type.isEnum() && previous != null && previous.equals(newVal)) {
			update = false;	
		}
		else if(type.isPrimitive() && previous != null && previous.equals(newVal)) {
			update = false;			
		}
		else if(type.equals(String.class) && previous != null && previous.equals(newVal)) {
			update = false;
		}

		return update;
	}
}
