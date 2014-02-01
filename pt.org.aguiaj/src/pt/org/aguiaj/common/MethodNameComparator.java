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
package pt.org.aguiaj.common;

import java.lang.reflect.Method;
import java.util.Comparator;

import pt.org.aguiaj.standard.StandardNamePolicy;

public class MethodNameComparator implements Comparator<Method> {
	@Override
	public int compare(Method a, Method b) {
		String prettyA = StandardNamePolicy.prettyPropertyName(a);
		String prettyB = StandardNamePolicy.prettyPropertyName(b);
		int i = 0;
		if(a.getDeclaringClass().isAssignableFrom(b.getDeclaringClass()))
			i--;
		else if(b.getDeclaringClass().isAssignableFrom(a.getDeclaringClass()))
			i++;
				
		return i != 0 ? prettyA.compareTo(prettyB) : i;
	}
}
