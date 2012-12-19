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
import java.util.Arrays;

import pt.org.aguiaj.objects.ObjectModel;

public class ArrayObjectCreationCommand extends JavaCommandWithReturn {
	private Class<?> componentType;
	private int[] indexes;
	private String reference;
	private Object resultingObject;		

	public static final int UNDEFINED = -1;
	
	public ArrayObjectCreationCommand(Class<?> componentType, int[] indexes) {
		this(componentType, indexes, 
				ObjectModel.getInstance().nextReference(arrayType(componentType, indexes.length)));
	}
		
	public ArrayObjectCreationCommand(Class<?> componentType, int[] indexes, String reference) {
		this.componentType = componentType;
		this.indexes = indexes;
		this.reference = reference;
	}

	private static Class<?> arrayType(Class<?> componentType, int depth) {
		Class<?> arrayType = componentType;
		while(depth != 0) {
			arrayType = Array.newInstance(arrayType, 0).getClass();
			depth--;
		}
		return arrayType;
	}
	
	public String getJavaInstruction() {
		String type = componentType.getSimpleName();
		return type + brackets() + " " + reference + " = new " + type + sizes();
	}

	private String brackets() {
		String ret = "";
		for(int i = 0; i < indexes.length; i++)
			ret += "[]";
		
		return ret;
	}
	
	private String sizes() {
		String ret = "";
		for(int i = 0; i < indexes.length; i++)
			ret += "[" + (indexes[i] == UNDEFINED ? "" : indexes[i])+ "]";
		
		return ret;
	}
	
	public void execute() {
		int nullIndex = 1;
		while(nullIndex < indexes.length) {
			if(indexes[nullIndex] == UNDEFINED)
				break;
			else
				nullIndex++;
		}
		
		for(int i = nullIndex; i < indexes.length; i++)
			componentType = Array.newInstance(componentType, 0).getClass();
		
		resultingObject = Array.newInstance(componentType, Arrays.copyOf(indexes, nullIndex));
	}
	
	public String getReference() {
		return reference;
	}
	
	
	public Object getResultingObject() {
		return resultingObject;
	}

	@Override
	public Class<?> getReferenceType() {
		return Array.newInstance(componentType, new int[indexes.length]).getClass();
	}
}
