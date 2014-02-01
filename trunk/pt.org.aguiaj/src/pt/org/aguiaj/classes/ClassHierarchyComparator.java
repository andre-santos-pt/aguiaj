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
package pt.org.aguiaj.classes;

import java.util.Comparator;

public class ClassHierarchyComparator implements Comparator<Class<?>> {
	public int compare(Class<?> a, Class<?> b) {
		if(a.equals(b))
			return 0;
		else if(a.isAssignableFrom(b))
			return -1;
		else
			return 1;
	}
}
