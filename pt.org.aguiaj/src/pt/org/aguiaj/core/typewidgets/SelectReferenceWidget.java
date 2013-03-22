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

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.common.Reference;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.objects.ObjectModel;

public class SelectReferenceWidget extends ReferenceTypeWidget {
	private static final int NULL_INDEX = 0;
	private static final String NULL_ITEM = "null";
	private static final String NA_KEY = "(...)";

	private static final String STRING_REGEX = "\"(.)*\"";
	
	private int lastSelectionIndex;
	private Combo combo;
	private WidgetProperty type;
	private final int NA_INDEX;

	private Composite border;

	private ObjectModel.EventListener listener;

	public SelectReferenceWidget(final Composite parent, Class<?> clazz, final WidgetProperty type) {
		super(parent, SWT.NONE, clazz, type, true);
		this.type = type;
		setLayout(new RowLayout());
		NA_INDEX = type == WidgetProperty.PARAMETER ? 0 : 1;
		createContents2(border);
	}

	@Override
	protected void createContents(Composite parent) {
		border = parent;
	}

	private void modifyField() {
		if(combo.getSelectionIndex() == NA_INDEX) {
			combo.select(lastSelectionIndex);
		}
		else {
			String item = getTextualRepresentation();						
			switch(type) {
			case ATTRIBUTE:
				modifyAttribute(item); 
				break;
			case ARRAYPOSITION: 
				modifyArrayPosition(item);
				break;
			}
			lastSelectionIndex = combo.getSelectionIndex();	
		}
	}
	
	private void createContents2(Composite parent) {
		if(ClassModel.getInstance().isPolymorphic(getType()) &&
				ClassModel.getInstance().isPluginTypeActive(getType()))
			new IconWidget(this, getType());

//		int style = SWT.READ_ONLY;
//		if(getType().equals(String.class))
//			style = SWT.DROP_DOWN;

		combo = new Combo(this, SWT.DROP_DOWN);

		if(getType().equals(String.class))
			combo.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					String text = combo.getText();
					if(text.equals("null")) {
						combo.setForeground(AguiaJColor.BLACK.getColor());
					}
					else {
						if(combo.getData(text) == null && !(getType().isAssignableFrom(String.class) && text.matches(STRING_REGEX))) {	
							combo.setForeground(AguiaJColor.ALERT.getColor());
						}
						else {
							combo.setForeground(AguiaJColor.BLACK.getColor());
						}
					}
				}
			});

		combo.setLayoutData(new RowData(75, 22));	

		if(type == WidgetProperty.ATTRIBUTE || type == WidgetProperty.ARRAYPOSITION) {
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {					
					modifyField();
				}
			});

			combo.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.keyCode == SWT.CR) {
						modifyField();
					}
				}
			});
		}

		setObjects(ObjectModel.getInstance().getCompatibleReferences(getType()));

		listener = new ObjectModel.EventListenerAdapter() {
			@Override
			public void newReferenceEvent(Reference ref) {
				if(getType().isAssignableFrom(ref.type)) {
					setObjects(ObjectModel.getInstance().getCompatibleReferences(getType()));
				}
			}

			@Override
			public void removeReferenceEvent(Reference ref) {
				if(getType().isAssignableFrom(ref.type)) {
					setObjects(ObjectModel.getInstance().getCompatibleReferences(getType()));
				}
			}

			@Override
			public void clearAll() {
				List<Reference> empty = Collections.emptyList();
				if(!combo.isDisposed())
					setObjects(empty);
			}
		};

		ObjectModel.getInstance().addEventListener(listener);

		combo.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				ObjectModel.getInstance().removeEventListener(listener);
			}
		});

		combo.select(NULL_INDEX);
	}


	private void setObjects(List<Reference> newReferences) {
		int selectedIndex = combo.getSelectionIndex();
		String selected = null;
		Object object = getObject();

		if(selectedIndex != -1 && selectedIndex != NULL_INDEX) 
			selected = combo.getItem(selectedIndex);

		combo.removeAll();
		combo.add(NULL_ITEM, NULL_INDEX);
		combo.setData(NULL_ITEM, null);
		
		lastSelectionIndex = NA_INDEX;

		if(type != WidgetProperty.PARAMETER)
			combo.add(NA_KEY, NA_INDEX);

		boolean containsSelected = false;
		for(Reference ref : newReferences) {
			combo.add(ref.name);			
			combo.setData(ref.name, ref.object);
			if(ref.name.equals(selected))
				containsSelected = true;
		}

		for(int i = 0; i < combo.getItemCount(); i++) {
			if(containsSelected && combo.getItem(i).equals(selected) ||
					!containsSelected && combo.getData(combo.getItem(i)) == object) {
				combo.select(i);
				lastSelectionIndex = i;
				break;
			}	
		}
	}


	public Object getObject() {
//		int selectionIndex = -1;

//		if(combo != null && !combo.isDisposed())
//			selectionIndex = combo.getSelectionIndex();

		if(combo.isDisposed())
			return null;
		
		if(getType().equals(String.class)) {
			String text = combo.getText();
			if(text.matches("\"(.)*\""))
				return text.substring(1, text.length()-1);
		}

		return combo.getData(combo.getText());
		//		return selectionIndex <= 0 ? null : combo.getData(combo.getItem(selectionIndex));
	}


	@Override
	public String getTextualRepresentation() {	
		if(combo.isDisposed())
			return null;

		String text = combo.getText();
		
		if(getType().isAssignableFrom(String.class) && text.matches("\"(.)*\"")) {
			return text;
		}	

		if(combo.getData(text) == null || combo.getSelectionIndex() == -1)
			return null;
		else
			return combo.getItem(combo.getSelectionIndex());
	}


	public void update(Object object) {
		if(combo.isDisposed())
			return;

		if(object == null) {
			combo.select(NULL_INDEX);
		}
		else {
			boolean changed = false;

			for(int i = NA_INDEX + 1; i < combo.getItemCount() && !changed; i++) {
				if(combo.getData(combo.getItem(i)) == object) {
					combo.select(i);
					lastSelectionIndex = i;
					changed = true;
				}
			}

			if(!changed) {
				if(type == WidgetProperty.PARAMETER) {
					combo.select(NULL_INDEX);
				}
				else {
					combo.select(NA_INDEX);
					combo.setData(NA_KEY, object);
					lastSelectionIndex = NA_INDEX;
				}
			}
		}
	}

	public Control getControl() {
		return combo;
	}
}
