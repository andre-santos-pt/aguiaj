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

public class CharacterLiteral extends Literal {
	
	private final static String regex = "'.'";
	
	protected CharacterLiteral() {
		super(char.class);		
	}

	@Override
	public boolean accept(String text) {
		if(text.matches(regex)) {
			value = text.charAt(1);
			return true;
		}		
		else
			return false;
	}
	
}
