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

public class CharArray2D extends ArrayLiteral {
	
	protected CharArray2D() {
		super(char[][].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		char[][] matrix = new char[parts.length][];
		for(int i = 0; i < matrix.length; i++) {
			CharArray charArray = new CharArray();
			if(!charArray.accept(parts[i]))
				return null;
			
			matrix[i] = (char[]) charArray.resolve();					
		}
		return matrix;
	}
}
