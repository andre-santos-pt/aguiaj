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
package pt.org.aguiaj.core.typewidgets;

import java.util.EnumSet;
import java.util.Set;

public enum WidgetProperty { 
	ATTRIBUTE, 
	ARRAYPOSITION, 
	PROPERTY, 
	PARAMETER, 
	OBJECT_WIDGET,
	
	MODIFIABLE,
	NO_EXTENSION;
	
	private static Set<WidgetProperty> ownerTypes = 
		EnumSet.of(ATTRIBUTE, ARRAYPOSITION, PROPERTY, PARAMETER, OBJECT_WIDGET);
	
	public static boolean isValidSet(Set<WidgetProperty> set) {
		int c = 0;
		for(WidgetProperty ownerType : ownerTypes)
			if(set.contains(ownerType))
				c++;
		
		return c == 1;
	}
	
	public static WidgetProperty getOwnerType(Set<WidgetProperty> set) {
		for(WidgetProperty ownerType : ownerTypes)
			if(set.contains(ownerType))
				return ownerType;
		
		return null;				
	}
	
	
}
