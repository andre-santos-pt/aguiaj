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
package pt.org.aguiaj.aspects;

import java.util.Iterator;
import java.util.List;

import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.core.typewidgets.SelectReferenceWidget;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;


public aspect ReferenceWidgetUpdates {
	private final Multimap<Class<?>, SelectReferenceWidget> selectWidgetsTable;

	public ReferenceWidgetUpdates() {
		selectWidgetsTable = LinkedListMultimap.create();
	}

	after(SelectReferenceWidget widget) : 
		execution(SelectReferenceWidget.new(..)) && this(widget){
		registerSelectWidget(widget);
	}

	after(Class<?> refType, Object object, String reference) : 
		execution(void ObjectModelRefactoraddReference(Class<?>, Object, String)) && 
		args(refType, object, reference) {
		updateWidgets(refType);
	}

	void around (Object object) : 
		execution(void ObjectModelRefactorremoveObject(Object)) && args(object) {
		List<Reference> refs = ObjectModelRefactor.getInstance().getReferences(object);
		proceed(object);
		for(Reference r : refs)
			updateWidgets(r.type);
	}

	void around(String reference) :
		execution(void ObjectModelRefactorremoveReference(String)) && args(reference) {
		Class<?> refType = ObjectModelRefactor.getReferenceType(reference);
		proceed(reference);
		updateWidgets(refType);			
	}


	private void registerSelectWidget(SelectReferenceWidget widget) {
		Class<?> refType = widget.getReferenceType();

		selectWidgetsTable.put(refType, widget);		
		List<Reference> refs = ObjectModelRefactor.getInstance().getCompatibleReferences(refType);
		widget.setObjects(refs);
	}

	private void updateWidgets(Class<?> refType) {	
		for(Class<?> clazz : selectWidgetsTable.keySet()) {
			if(clazz.isAssignableFrom(refType)) {		
				Iterator<SelectReferenceWidget> it = selectWidgetsTable.get(clazz).iterator();
				while(it.hasNext()) {
					SelectReferenceWidget widget = it.next();
					if(widget.isDisposed()) {
						it.remove();
					}
					else {
						List<Reference> refs = ObjectModelRefactor.getInstance().getCompatibleReferences(clazz);
						widget.setObjects(refs);
					}
				}
			}
			else if(refType.isArray() && refType.getComponentType().isAssignableFrom(clazz)) {
				updateWidgets(refType.getComponentType());
			}
		}
	}

}
