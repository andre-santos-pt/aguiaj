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

import static pt.org.aguiaj.core.ReflectionUtils.getTextualRepresentation;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.Fonts;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.extensibility.VisualizationWidget;

@PluggableObjectWidget(char[].class)
public class StringObjectWidget extends VisualizationWidget.Adapter<Object> {
	private Composite section;
	private Text text;

	private String previousContent;
	
	private boolean needsRelayout;

	@Override
	public void createSection(Composite section) {
		this.section = section;		
		section.setLayout(new RowLayout(SWT.HORIZONTAL));
		text = new Text(section, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI);
		Fonts.set(text, "Courier", AguiaJParam.MEDIUM_FONT);
		needsRelayout = true;
		previousContent = null;
	}


	public void update(Object object) {
		if(getTextualRepresentation(object, true).equals(previousContent)) {
			needsRelayout = false;
		}		
		else {
			needsRelayout = true;
			if(object == null) {
				previousContent = null;
				text.setText("");
			}
			else {
				String toString = ReflectionUtils.getTextualRepresentation(object, false);
					
				if(toString != null)
					text.setText(toString);
				else
					text.setText("null");
				
				section.layout();
				section.pack();
				
				previousContent = ReflectionUtils.getTextualRepresentation(object, true);
			}
		}
	}
	
	
	@Override
	public boolean needsRelayout() {
		return needsRelayout;
	}
}
