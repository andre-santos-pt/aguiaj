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
package pt.org.aguiaj.core.interpreter;

public class DoubleArray extends ArrayLiteral {
	
	protected DoubleArray() {
		super(double[].class);		
	}	

	@Override
	protected Object accept(String[] parts) {
		double[] array = new double[parts.length];
		boolean oneWithDot = false;
		for(int i = 0; i < array.length; i++) {
			try {
				if(parts[i].indexOf('.') != -1)
					oneWithDot = true;
				
				array[i] = Double.parseDouble(parts[i]);					
			}
			catch(NumberFormatException e) {
				return null;
			}
		}
		return oneWithDot ? array : null;
	}
}
