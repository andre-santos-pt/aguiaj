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
package pt.org.aguiaj.core.commands;


import pt.org.aguiaj.aspects.ObjectModel;


public class CommandsCommon {
//	public static Object fetchEnumConstant(String objectReference) throws ParseException {
//		Object object = null;
//		String[] split = objectReference.split("\\.");
//		String type = split[0];
//		String constant = split[1];
//		
//		ClassModel.checkType(type);
//		
//		Class<?> enumType = ClassModel.getClassInUse(type);
//		Field f;
//		try {
//			f = enumType.getField(constant);
//		} 
//		catch (Exception e) {
//			throw new ParseException("Enum constant not found", constant);
//		} 
//		
//		if(!f.isEnumConstant())
//			throw new ParseException("Enum constant not found", constant);
//		
//		try {
//			object = f.get(null);
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//		} 
//		
//		return object;
//	}
	
	public static String buildParams(Object[] args) {
		String params = "";
		for(Object obj : args) {
			String param = null;
			if(obj == null)
				param = "null";
			else {
				Class<?> clazz = obj.getClass();
				if(clazz.equals(Boolean.class) || clazz.equals(Integer.class) || clazz.equals(Double.class))
					param = obj.toString();
				else if(clazz.equals(Character.class))
					param = "'" + obj.toString() + "'";				
				else if(clazz.equals(String.class))
					param = "\"" + obj.toString() + "\"";
				else if(clazz.isEnum())
					param = clazz.getSimpleName() + "." + ((Enum<?>) obj).name();
				else {
					param = ObjectModel.getFirstReference(obj).name;
				}
			}
			params += (params.equals("") ? "" : ", ") + param;
		}
		return params;
	}		
}
