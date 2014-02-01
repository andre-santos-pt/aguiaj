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

public class CharArray extends ArrayLiteral {
	
	protected CharArray() {
		super(char[].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		char[] array = new char[parts.length];
		for(int i = 0; i < array.length; i++) {
			if(!parts[i].matches("'.'"))
				return null;
			
			array[i] = parts[i].charAt(1);								
		}
		return array;
	}
}
