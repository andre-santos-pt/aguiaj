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

public class BooleanArray2D extends ArrayLiteral {
	
	protected BooleanArray2D() {
		super(boolean[][].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		boolean[][] matrix = new boolean[parts.length][];
		for(int i = 0; i < matrix.length; i++) {
			BooleanArray booleanArray = new BooleanArray();
			if(!booleanArray.accept(parts[i]))
				return null;
			
			matrix[i] = (boolean[]) booleanArray.resolve();					
		}
		return matrix;
	}
}
