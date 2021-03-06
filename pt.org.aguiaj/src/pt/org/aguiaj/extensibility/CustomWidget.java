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
package pt.org.aguiaj.extensibility;

/**
 * Represents a custom widget that can be plugged into the AguiaJ framework.
 *
 * @param <T> The type of domain object that this widget renders.
 */
public interface CustomWidget<T> {

	 /**
	  * Update the widget given the domain object.
	  * 
	  * @param object Object to update. Contract: will never be passed null
	  */
	void update(T object);
	
}
