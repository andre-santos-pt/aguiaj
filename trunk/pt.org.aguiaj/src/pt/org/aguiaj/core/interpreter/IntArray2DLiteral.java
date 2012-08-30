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
package pt.org.aguiaj.core.interpreter;

public class IntArray2DLiteral extends ArrayLiteral {
	
	protected IntArray2DLiteral() {
		super(int[][].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		int[][] matrix = new int[parts.length][];
		for(int i = 0; i < matrix.length; i++) {
			IntArray intArray = new IntArray();
			if(!intArray.accept(parts[i]))
				return null;
			
			matrix[i] = (int[]) intArray.resolve();					
		}
		return matrix;
	}
}
