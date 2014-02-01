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

public class DoubleArray extends ArrayLiteral {
	
	protected DoubleArray() {
		super(double[].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		double[] array = new double[parts.length];
		for(int i = 0; i < array.length; i++) {
			try {
				array[i] = Double.parseDouble(parts[i]);					
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
		return array;
	}
	
}
