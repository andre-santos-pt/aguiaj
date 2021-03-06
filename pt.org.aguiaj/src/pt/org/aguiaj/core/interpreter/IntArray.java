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

public class IntArray extends ArrayLiteral {

	protected IntArray() {
		super(int[].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		int[] array = new int[parts.length];
		for(int i = 0; i < array.length; i++) {
			IntLiteral intLiteral = new IntLiteral();
			if(!intLiteral.accept(parts[i]))
				return null;
			array[i] = (Integer) intLiteral.resolve();			
		}
		return array;
	}
}
