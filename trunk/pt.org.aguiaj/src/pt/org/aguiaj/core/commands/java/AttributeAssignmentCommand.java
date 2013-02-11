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
package pt.org.aguiaj.core.commands.java;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.objects.ObjectModel;

public class AttributeAssignmentCommand extends JavaCommand {	
	private Object owner;
	private Field field;
	private Object value;
	private String reference;
	
	public AttributeAssignmentCommand(Object owner, Field field, Object value, String reference) {
		assert field != null;
		Class<?> type = field.getType();
	
		assert 
		!type.isPrimitive() && (value == null || type.isInstance(value)) ||
		type.equals(int.class) && Integer.class.isInstance(value) ||
		type.equals(double.class) && Double.class.isInstance(value) ||
		type.equals(boolean.class) && Boolean.class.isInstance(value) ||
		type.equals(char.class) && Character.class.isInstance(value);	
		
		assert 
			Modifier.isStatic(field.getModifiers()) && owner == null ||
			!Modifier.isStatic(field.getModifiers()) && owner != null;
		
		
		this.owner = owner;
		this.field = field;
		this.value = value;
		this.reference = reference;
	}
	
	
	public String getJavaInstruction() {
		String refName = null;
		
		if(Modifier.isStatic(field.getModifiers())) {
			refName = field.getDeclaringClass().getSimpleName();
		}
		else {
			Reference ref = ObjectModel.getFirstReference(owner);
			if(ref != null)
				refName = ref.name;
		}
		
		if(refName == null)
			return null;
	
		String val = "null";
		if(value != null) {
			Class<?> clazz = value.getClass();
			if(clazz.equals(Integer.class) || clazz.equals(Double.class) || clazz.equals(Boolean.class)) {
				val = value.toString();
			}
			else if(clazz.equals(Character.class)) {
				val = "'" + value.toString() + "'";
			}
			else if(clazz.equals(String.class)) {
				val = "\"" + value.toString() + "\"";
			}
			else if(field.getType().isEnum() && reference == null) {
				val = ((Enum<?>) value).getClass().getSimpleName() + "." + ((Enum<?>) value).name();				
			}
			else {
				val = reference;
			}
		}
		
		return refName + "." + field.getName() + " = " + val;
	}

	
	public void execute() {
		try {
			field.set(owner, value);
		}
		catch(RuntimeException runtimeEx) {
			throw runtimeEx;
		}
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	@Override
	public String getReference() {
		return reference;
	}
}
