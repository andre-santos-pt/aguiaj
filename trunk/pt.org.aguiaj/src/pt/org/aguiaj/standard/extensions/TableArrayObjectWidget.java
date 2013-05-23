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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import pt.org.aguiaj.standard.StandardNamePolicy;


public class TableArrayObjectWidget implements VisualizationWidget<Object> {

	private Table table;
	private Object object;
	private List<Field> fields;
	private List<Method> properties;
	private String[][] data;
	private Color gray;
	
	
	@Override
	public void createSection(Composite section) {
		section.setLayout(new FillLayout());
		table = new Table(section, SWT.BORDER);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		gray = section.getBackground();
	}

	@Override
	public boolean needsRelayout() {
		return true;
	}

	@Override
	public Control getControl() {
		return table;
	}

	@Override
	public void update(Object object) {
		if(this.object == null || !this.object.getClass().equals(object.getClass())) {
			fields = ClassModel.getInstance().getVisibleAttributes(object.getClass().getComponentType());
			properties = ClassModel.getInstance().getAccessorMethods(object.getClass().getComponentType());
		}
		this.object = object;
		
		String[][] tmp = tableData((Object[]) object);

		if(!Arrays.deepEquals(data, tmp)) {
			data = tmp;
			redrawTable();
		}
	}

	private void redrawTable() {
		table.removeAll();
		for(TableColumn col : table.getColumns()) {
			col.dispose();
		}

		// index column
		new TableColumn (table, SWT.NONE);

		for(Field f : fields) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (f.getName());
			column.setToolTipText(StandardNamePolicy.prettyField(f));
		}
		for(Method m : properties) {			
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (StandardNamePolicy.prettyPropertyName(m));
			column.setToolTipText(StandardNamePolicy.signature(m));
		}
		
		for(int i = 0; i < data.length; i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			// index column
			item.setText(0, Integer.toString(i));			
			item.setBackground(0, gray);

			if(data[i] != null) {
				for(int j = 0; j < data[i].length; j++) {
					item.setText(j+1, data[i][j] == null ? "null" : data[i][j]);
				}
			}
		}

		for(TableColumn col : table.getColumns()) {
			col.pack();
		}
		table.pack();
		table.layout();
	}

	private String[][] tableData(Object[] array) {
		int nCols = fields.size() + properties.size();
		String[][] data = new String[array.length][];
		for(int i = 0; i < array.length; i++) {
			Object element = Array.get(object, i);			
			if(element != null) {
				data[i] = new String[nCols];
				int j = 0;
				for(Field f : fields) {
					Object val = null;
					try {
						val = f.get(element);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					data[i][j++] = val == null ? "null" : val.toString();
				}

				for(Method m : properties) {							
					MethodInvocationCommand cmd = new MethodInvocationCommand(element, "", m);
					ExceptionHandler.INSTANCE.execute(cmd);

//					cmd.execute();
					Object val = cmd.getResultingObject();
					data[i][j++] = val == null ? "null" : val.toString();
				}
			}
		}
		return data;
	}

}
