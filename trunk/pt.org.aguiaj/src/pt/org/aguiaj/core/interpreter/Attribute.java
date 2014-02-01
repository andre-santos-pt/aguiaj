/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.org.aguiaj.core.interpreter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.extensibility.Reference;

// TODO: array.length support
public class Attribute extends Expression implements Assignable {
	private ExistingReference existingReference;
	private Field attribute;
	private Class<?> classAttribute;
	private boolean isArrayLengthField;

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {

		String tmp = text.trim().replaceAll("\\s", "");
		if(!tmp.matches("[\\S]+(\\.)[\\S]+"))
			return false;

		String[] parts = tmp.split("\\.");
		if(parts.length != 2)
			return false;

		String ref = parts[0];
		String att = parts[1];

		Class<?> clazz = null;

		existingReference = new ExistingReference();
		if(existingReference.acceptText(ref, referenceTable, classSet))
			clazz = referenceTable.get(ref).type;

		if(clazz == null) {
			clazz = Common.findClass(classSet, ref);
			if(clazz != null && att.equals("class")) {
				classAttribute = clazz;
				return true;
			}
		}

		if(clazz == null)
			return false;

		if(clazz.isArray() && att.equals("length")) {
			isArrayLengthField = true;
			return true;
		}
		else {
			for(Field f : clazz.getFields())
				if(f.getName().equals(att)) {
					attribute = f;
					attribute.setAccessible(true);
				}

			if(attribute == null)
				return false;
		}

		return true;
	}

	@Override
	public Class<?> type() {
		if(isClassAttribute())
			return Class.class;
		else if(isArrayLengthField)
			return int.class;
		else 
			return attribute.getType();
	}

	public boolean isClassAttribute() {
		return classAttribute != null;
	}

	@Override
	public Object resolve() {
		if(isClassAttribute()) {
			return classAttribute;
		}
		else if(isArrayLengthField) {
			Object array = existingReference.resolve();
			if(array == null)
				throw new NullPointerException("Reference is null: " + existingReference.getExpressionText());
				
			return Array.getLength(array);
		}
		else {
			try {
				return attribute.get(existingReference.resolve());
			} 
			catch (NullPointerException ex) {
				throw new NullPointerException("Reference is null: " + existingReference.getExpressionText());
			} 
			catch (IllegalAccessException e) {
				e.printStackTrace();
			} 
		}
		return null;
	}

	public Field field() {
		return attribute;
	}

	@Override
	public boolean canAssign() {		
		return !Modifier.isFinal(attribute.getModifiers()) && !isClassAttribute();
	}

	@Override
	public String getExpressionText() {
		return getText();
	}

	public Object getOwnerObject() {
		return existingReference.resolve();
	}
}
