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

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.extensibility.Reference;


public abstract class Expression {
	private String text;
	
	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return getText();
	}
	
	public final boolean acceptText(
			String text, 
			Map<String, Reference> referenceTable, 
			Set<Class<?>> classSet)
			throws ParseException {
		
		this.text = text;
		return accept(text, referenceTable, classSet);
	}
	
	public abstract Class<?> type();
	
	// syntax
	protected abstract boolean accept(
			String text, 
			Map<String, Reference> referenceTable, 
			Set<Class<?>> classSet) 
			throws ParseException;	
	
	
	// semantics
	public abstract Object resolve() throws ParseException;
	
}
