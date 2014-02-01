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
package pt.org.aguiaj.core.documentation;
class ControlAnchor {
	final Class<?> clazz;
	final String anchor;

	public ControlAnchor(Class<?> clazz, String anchor) {
		this.clazz = clazz;
		this.anchor = anchor;
	}
}
