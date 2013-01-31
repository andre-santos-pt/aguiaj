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

public class DoubleLiteral extends Literal {
	
	protected DoubleLiteral() {
		super(double.class);		
	}

	@Override
	public boolean accept(String text) {
		try {
			value = Double.parseDouble(text);	
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
	
}
