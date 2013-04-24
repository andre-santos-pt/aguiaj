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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.ArrayPositionAssignmentCommand;
import pt.org.aguiaj.core.commands.java.AttributeAssignmentCommand;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.objects.ObjectModel;


public abstract class AbstractTypeWidget extends Composite implements TypeWidget {

	private final WidgetProperty type;
	private final boolean modifiable;
	
	private Field attributeField;
	private Object attributeOwner;

	private Object arrayObject;
	private int lineArray;
	
	public AbstractTypeWidget(Composite parent, int style, WidgetProperty type, boolean modifiable) {
		super(parent, style);
		
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		setLayout(layout);
		
		this.type = type;
		this.modifiable = modifiable;
		
		lineArray = -1;	
		
		createContents(this);
	}
	
	protected abstract void createContents(Composite parent);
	
	public void setAttribute(Field attributeField, Object attributeOwner) {
		this.attributeField = attributeField;
		this.attributeField.setAccessible(true);
		this.attributeOwner = attributeOwner;
	}

	public void setArrayPosition(Object arrayObject, int line) {
		this.arrayObject = arrayObject;
		lineArray = line;
	}

	public WidgetProperty getUsageType() {
		return type;
	}
	
	public boolean isModifiable() {
		return modifiable;
	}
	
	protected final void modifyAttribute() {
		modifyAttribute(null);
	}

	protected final void modifyAttribute(String expression) {
		JavaCommand cmd = new AttributeAssignmentCommand(attributeOwner, attributeField, getObject(), expression);
		ObjectModel.getInstance().execute(cmd);
		try {
			Object val = attributeField.get(attributeOwner);
			update(val);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	protected final void modifyArrayPosition(String expression) {
		String ref = ObjectModel.getFirstReference(arrayObject).name;
		JavaCommand cmd = new ArrayPositionAssignmentCommand(arrayObject, ref, new int[] {lineArray}, getObject(), expression);
		ObjectModel.getInstance().execute(cmd);
	}


	protected final void addEnterKeyListener(final Control control) {
		control.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent event) {
				if(event.keyCode == SWT.CR) {
					switch(type) {
					case ATTRIBUTE: 
						modifyAttribute(); 
						break;
					case ARRAYPOSITION: 
						modifyArrayPosition(null); 
						break;
					}
				}
			}
		});
	}
	
	protected boolean isFinalAttribute() {
		return attributeField != null && Modifier.isFinal(attributeField.getModifiers());
	}
	
	public void setToolTipText(String text) {
		if(getControl() != null)
			getControl().setToolTipText(text);
	}
	
	public String getToolTipText() {
		if(getControl() != null)
			return getControl().getToolTipText();
		
		return null;
	}	
}
