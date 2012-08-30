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
package pt.org.aguiaj.aspects;

import java.util.Arrays;

class IdentityObjectSet {
		private Object[] objects;
		private int next;
		
		public IdentityObjectSet() {
			objects = new Object[100];
			next = 0;
		}
		
		private void increaseSpace() {
			objects = Arrays.copyOf(objects, objects.length * 2);
		}
		
		public boolean contains(Object obj) {
			for(int i = 0; i < next; i++)
				if(objects[i] == obj)
					return true;
			return false;
		}
		
		public Object[] objects() {
			return Arrays.copyOf(objects, next);
		}
		
		public void add(Object obj) {			
			if(!contains(obj)) {
				if(next == objects.length)
					increaseSpace();
				
				objects[next] = obj;
				next++;
			}
		}
		
		public void remove(Object obj) {
			for(int i = 0; i < next; i++) {
				if(objects[i] == obj) {
					next--;
					objects[i] = objects[next];
					objects[next] = null;
					return;
				}
			}
			throw new AssertionError("Object does not exist");
		}
	}
