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
package pt.org.aguiaj.core.commands.java;

import java.lang.reflect.Array;

public class ArrayPositionAssignmentCommand extends JavaCommand {

	private Object arrayObject;
	private String arrayReference;
	private int[] indexes;
	private Object value;
	private String valueText;

	public ArrayPositionAssignmentCommand(Object arrayObject, String arrayReference, int[] indexes, Object value, String valueText) {
		this.arrayObject = arrayObject;
		this.arrayReference = arrayReference;
		this.indexes = indexes;
		this.value = value;
		this.valueText = valueText;
	}



	public String getJavaInstruction() {
		String val = valueText;
		if(val == null)
			val = value == null ? "null" : value.toString();

		if(valueText == null && value != null) {
			if(arrayObject.getClass().getComponentType().isEnum()) {
				val = ((Enum<?>) value).getClass().getSimpleName() + "." + ((Enum<?>) value).name();
			}			
			else if(arrayObject.getClass().getComponentType().equals(char.class)) {
				val = "'" + val + "'";			
			}
			else if(arrayObject.getClass().getComponentType().equals(String.class)) {
				val = "\"" + val + "\"";			
			}
			else if(!arrayObject.getClass().getComponentType().isPrimitive()) {
				val = valueText;
			}
		}

		return arrayReference + indexesText() + " = " + val;
	}

	private String indexesText() {
		String ret = "";
		for(int i : indexes)
			ret += "[" + i + "]";
		return ret;
	}

	public void execute() {
		Object array = arrayObject;
		int last = indexes.length - 1;
		for(int i = 0; i < last; i++)
			array = Array.get(array, indexes[i]);

		try {
			Array.set(array, indexes[last], value);
		}
		catch(RuntimeException runtimeEx) {
			throw runtimeEx;
		}
	}
}
