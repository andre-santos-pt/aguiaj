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

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;


public class FilterComparator implements Comparator<Method> {
		private final List<ClassMemberFilter> filters;

		FilterComparator(List<ClassMemberFilter> filters) {
			this.filters = filters;
		}

		@Override
		public int compare(Method m1, Method m2) {
			int m1Index = 0;
			int m2Index = 0;
			for(ClassMemberFilter f : filters) {
				if(f instanceof NameBasedFilter) {
					String[] methodNames = ((NameBasedFilter) f).getMethodNames();							
					for(int i = 0; i < methodNames.length; i++) {
						if(m1.getName().equals(methodNames[i]))
							m1Index = i;
						else if(m2.getName().equals(methodNames[i]))
							m2Index = i;
					}
				}
			}
			return new Integer(m1Index).compareTo(m2Index);
		}
	}
