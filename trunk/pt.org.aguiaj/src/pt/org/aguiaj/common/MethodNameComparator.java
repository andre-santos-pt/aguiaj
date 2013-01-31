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
package pt.org.aguiaj.common;

import java.lang.reflect.Method;
import java.util.Comparator;

import pt.org.aguiaj.standard.StandardNamePolicy;

public class MethodNameComparator implements Comparator<Method> {
	@Override
	public int compare(Method a, Method b) {
		String prettyA = StandardNamePolicy.prettyPropertyName(a);
		String prettyB = StandardNamePolicy.prettyPropertyName(b);
		return prettyA.compareTo(prettyB);
	}
}
