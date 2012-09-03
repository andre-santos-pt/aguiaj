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
package pt.org.aguiaj.core.typewidgets;

import org.eclipse.swt.widgets.Composite;



public abstract class ReferenceTypeWidget extends AbstractTypeWidget {

	private Class<?> clazz;

	public ReferenceTypeWidget(final Composite parent, int style, Class<?> clazz, WidgetProperty type, boolean modifiable) {
		super(parent, style, type, modifiable);
		this.clazz = clazz;
	}

	protected Class<?> getType() {
		return clazz;
	}
	
	public Class<?> getReferenceType() {
		return clazz;
	}

	
	public Object defaultValue() {
		return null;
	}

	
	
	
	public String toString() {
		Object obj = getObject();
		return obj != null ? obj.toString() : "NULL";
	}

}
