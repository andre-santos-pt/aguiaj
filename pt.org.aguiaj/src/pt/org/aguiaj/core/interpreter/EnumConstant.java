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
package pt.org.aguiaj.core.interpreter;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;

public class EnumConstant extends Expression implements Instruction {

	private Class<?> enumType;
	private Object value;

	@Override
	public boolean accept(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		int i = text.indexOf('.');
		if(i == -1)
			return false;

		String enumName = text.substring(0, i).trim();
		String fieldName = text.substring(i).trim();

		if(fieldName.length() == 1)
			return false;

		fieldName = fieldName.substring(1);

		if(!Common.isValidJavaIdentifier(enumName) || ! Common.isValidJavaIdentifier(fieldName))
			return false;

		enumType = Common.findClass(classSet, enumName);
		if(enumType == null || !enumType.isEnum())
			return false;

		for(Field f : enumType.getFields()) {
			if(f.isEnumConstant() && f.getName().equals(fieldName)) {
				try {
					value = f.get(null);
					break;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		if(value == null)
			throw new ParseException("Enum constant not found", fieldName);

		return true;
	}


	@Override
	public Class<?> type() {
		return enumType;
	}

	@Override
	public Object resolve() {
		return value;
	}

	@Override
	public JavaCommand getCommand() {		
		return new NewReferenceCommand(enumType, value, getText()); 	
	}

}
