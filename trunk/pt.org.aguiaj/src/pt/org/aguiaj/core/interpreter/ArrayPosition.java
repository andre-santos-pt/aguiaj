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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;


public class ArrayPosition extends Expression implements Assignable {
	private ExistingReference ref;
	private List<Expression> indexExpressions;

	@Override
	protected boolean accept(String text,
			Map<String, Reference> referenceTable, Set<Class<?>> classSet)
	throws ParseException {

		int i = text.indexOf('[');
		if(i == -1)
			return false;

		String refName = text.substring(0,i).trim();
		String pos = text.substring(i).trim();

		if(!pos.matches("(" + Common.arrayPositionRegex + ")+"))		
			return false;

		String[] indexesParts = Common.parts(pos);		
		indexExpressions = new ArrayList<Expression>();

		for(String part : indexesParts) {
			String expText = part.substring(1, part.length()-1).trim();
			Expression exp = ExpressionMatcher.match(expText, referenceTable, classSet);
			
			if(exp == null)
				throw new ParseException("Invalid expression", expText);
			
			indexExpressions.add(exp);
		}		

		ref = new ExistingReference();
		if(!ref.acceptText(refName, referenceTable, classSet))
			return false;			

		return true;
	}
	

	@Override
	public Class<?> type() {
		int n = indexExpressions.size();
		Class<?> type = ref.type().getComponentType();
		while(n != 1) {
			type = type.getComponentType();
			n--;
		}
		
		return type;
	}

	@Override
	public Object resolve() {
		Object array = arrayObject();
		int[] indexes = resolveIndexes(array);
		
		Object obj = Array.get(array, indexes[0]);
		for(int j = 1; j < indexes.length; j++) {
			obj = Array.get(obj, indexes[j]);
		}
		return obj;
	}

	public int[] resolveIndexes(Object arrayObject) {
		return Common.resolveIndexes(indexExpressions, arrayObject);
	}

	@Override
	public boolean canAssign() {
		return true;
	}
	
	public Object arrayObject() {
		return ref.resolve();
	}

	public String getIdentifier() {		
		return ref.getText();
	}

	@Override
	public String getExpressionText() {
		return getText();
	}
}
