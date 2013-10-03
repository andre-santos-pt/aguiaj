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
package pt.org.aguiaj.standard.extensions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.TypeWidget;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.typewidgets.WidgetFactory;
import pt.org.aguiaj.core.typewidgets.WidgetProperty;
import pt.org.aguiaj.extensibility.Reference;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import pt.org.aguiaj.objects.ObjectModel;


@PluggableObjectWidget({
	int[][].class, 
	double[][].class, 
	char[][].class, 
	boolean[][].class, 
	Object[][].class
})
public class Array2DObjectWidget extends VisualizationWidget.Adapter<Object> {
	private Composite section;
	private Object object;
	private List<TypeWidget> rows;
	private int numberOfRows;

	public void createSection(Composite section) {
		section.setLayout(new RowLayout(SWT.VERTICAL));
		this.section = section;
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
					Reference ref = ObjectModel.getFirstReference(Array2DObjectWidget.this.object);
					if(ref != null) {
						String source = ref.name + "[" + iFinal + "]";					
						Object obj = Array2DObjectWidget.this.getIndex(iFinal);
						Class<?> type = Array2DObjectWidget.this.object.getClass().getComponentType();
						ObjectModel.getInstance().execute(new NewReferenceCommand(type, obj, source));
					}
				}
			});

//			link.addObjectHighlightCapability(new ObjectToHighlightProvider() {
//
//				@Override
//				public Object getObjectToHighlight() {
//					return Array2DObjectWidget.this.getIndex(iFinal);
//				}
//			});

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
}
