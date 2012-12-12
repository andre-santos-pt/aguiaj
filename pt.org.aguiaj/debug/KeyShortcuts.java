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
package pt.org.aguiaj.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.objects.ObjectWidget;
import pt.org.aguiaj.objects.ReferenceObjectPairWidget;

public class KeyShortcuts {
	public static void addKeyShortcuts() {
		Display.getDefault().addFilter(SWT.KeyDown, new Listener() {			
			@Override
			public void handleEvent(Event event) {
				String key = Character.toString(event.character).toUpperCase();
				if(key.equals(AguiaJParam.OPERATIONS_KEY.getString())) {
		    		 Control control = Display.getDefault().getCursorControl();
		    		 Composite parent = crawlUp(control);
		    		 if(parent instanceof ObjectWidget) {
		    			 ObjectWidget widget = (ObjectWidget) parent;
		    			 widget.showOperations(!widget.isOperationsVisible());
		    		 }
				}
			}
		});
		Display.getDefault().addFilter(SWT.KeyDown, new Listener() {			
			@Override
			public void handleEvent(Event event) {
				String key = Character.toString(event.character).toUpperCase();
				if(key.equals(AguiaJParam.PRIVATES_KEY.getString())) {
		    		 Control control = Display.getDefault().getCursorControl();
		    		 Composite parent = crawlUp(control);
		    		 if(parent instanceof ObjectWidget) {
		    			 ObjectWidget widget = (ObjectWidget) parent;
		    			 widget.showPrivateAttributes(!widget.isPrivateAttributesVisible());
		    		 }
				}
			}
		});
		
		Display.getDefault().addFilter(SWT.KeyDown, new Listener() {			
			@Override
			public void handleEvent(Event event) {
				String key = Character.toString(event.character).toUpperCase();
				if(key.equals(AguiaJParam.PROPERTIES_KEY.getString())) {
		    		 Control control = Display.getDefault().getCursorControl();
		    		 Composite parent = crawlUp(control);
		    		 if(parent instanceof ObjectWidget) {
		    			 ObjectWidget widget = (ObjectWidget) parent;
		    			 widget.showProperties(!widget.isPropertiesVisible());
		    		 }
				}
			}
		});
		
		Display.getDefault().addFilter(SWT.KeyDown, new Listener() {			
			@Override
			public void handleEvent(Event event) {
				String key = Character.toString(event.character).toUpperCase();
				if(key.equals("C")) {
		    		 Control control = Display.getDefault().getCursorControl();
		    		 Composite parent = crawlUpReferencePair(control);
		    		 if(parent instanceof ReferenceObjectPairWidget) {
		    			 SWTUtils.saveImageToFile(parent, ((ReferenceObjectPairWidget) parent).getFirstReference() + ".png");
		    		 }
				}
			}
		});
	}
	
	private static ObjectWidget crawlUp(Control control) {
		if(control == null)
			return null;
		
		Composite parent = control.getParent();
		if(parent == null)
			return null;
		else if(parent instanceof ObjectWidget)
			return (ObjectWidget) parent;
		else
			return crawlUp(parent);
			
	}
	
	private static ReferenceObjectPairWidget crawlUpReferencePair(Control control) {
		if(control == null)
			return null;
		
		Composite parent = control.getParent();
		if(parent == null)
			return null;
		else if(parent instanceof ReferenceObjectPairWidget)
			return (ReferenceObjectPairWidget) parent;
		else
			return crawlUpReferencePair(parent);
			
	}
}
