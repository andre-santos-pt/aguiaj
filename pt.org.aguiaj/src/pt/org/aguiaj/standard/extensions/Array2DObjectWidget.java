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
package pt.org.aguiaj.standard.extensions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.common.widgets.LabelWidget.ObjectToHighlightProvider;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.extensibility.VisualizationWidget;

@PluggableObjectWidget({
	int[][].class, 
	double[][].class, 
	char[][].class, 
	boolean[][].class, 
	Object[][].class
})
public class Array2DObjectWidget implements VisualizationWidget<Object> {
	private Composite section;
	private Object object;
	private List<TypeWidget> rows;
	private int numberOfRows;

	public void createSection(Composite section) {
		section.setLayout(new RowLayout(SWT.VERTICAL));
		this.section = section;
//		section = new Composite(parent, SWT.NONE);
//		section.setBackground(parent.getBackground());
//		section.setLayout(new RowLayout(SWT.VERTICAL));
		rows = new ArrayList<TypeWidget>();
		numberOfRows = -1;
	}

	private void buildLines() {
		numberOfRows = Array.getLength(object);
		new ArrayLengthWidget(section).update(numberOfRows);
		
		for(int i = 0; i < numberOfRows; i++) {
			Composite row = new Composite(section, SWT.NONE);
			row.setLayout(new RowLayout(SWT.HORIZONTAL));
			final int iFinal = i;

			LabelWidget link = new LabelWidget.Builder()
			.text(Integer.toString(i))
			.small()
			.link()
			.create(row);

			link.addHyperlinkAction(new Listener () {
				public void handleEvent(Event event) {
					String ref = ObjectModel.getFirstReference(Array2DObjectWidget.this.object).name;
					String source = ref + "[" + iFinal + "]";					
					Object obj = Array2DObjectWidget.this.getIndex(iFinal);
					Class<?> type = Array2DObjectWidget.this.object.getClass().getComponentType();
					new NewReferenceCommand(type, obj, source).execute();
				}
			});

			link.addObjectHighlightCapability(new ObjectToHighlightProvider() {

				@Override
				public Object getObjectToHighlight() {
					return Array2DObjectWidget.this.getIndex(iFinal);
				}
			});

			rows.add(WidgetFactory.INSTANCE.createWidget(
					row, 
					object.getClass().getComponentType(),
					EnumSet.of(WidgetProperty.ARRAYPOSITION, WidgetProperty.NO_EXTENSION)));
		}
		SWTUtils.setColorRecursively(section, section.getBackground());
	}

	private Object getIndex(int i) {
		return Array.get(object, i);
	}

	public void update(Object object) {
		this.object = object;

		if(object != null) {
			if(numberOfRows == -1)
				buildLines();

			int i = 0;
			for(TypeWidget widget : rows) {
				widget.update(getIndex(i));
				i++;
			}
		}
	}

	@Override	
	public Control getControl() {
		return section;
	}


	@Override
	public boolean needsRelayout() {
		return true;
	}
}
