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

public class DoubleArray2D extends ArrayLiteral {
	
	protected DoubleArray2D() {
		super(double[][].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		double[][] matrix = new double[parts.length][];
		for(int i = 0; i < matrix.length; i++) {
			DoubleArray doubleArray = new DoubleArray();
			if(!doubleArray.accept(parts[i]))
				return null;
			
			matrix[i] = (double[]) doubleArray.resolve();					
		}
		return matrix;
	}
}
