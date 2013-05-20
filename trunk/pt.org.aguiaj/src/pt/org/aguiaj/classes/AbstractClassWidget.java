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
package pt.org.aguiaj.classes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.documentation.DocumentationLinking;
import pt.org.aguiaj.standard.StandardNamePolicy;

public class AbstractClassWidget extends Composite {

	public AbstractClassWidget(Composite parent, Class<?> clazz) {
		super(parent, SWT.BORDER);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		new IconWidget(this, clazz).setToolTipText("Polymorphic type");
		LabelWidget classNameLabel =  new LabelWidget.Builder()
			.text(StandardNamePolicy.prettyClassName(clazz))
			.big()
			.italic()
			.create(this);
		
		DocumentationLinking.add(classNameLabel.getControl(), clazz);
	}
}
