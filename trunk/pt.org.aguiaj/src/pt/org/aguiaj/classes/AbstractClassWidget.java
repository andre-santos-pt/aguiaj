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
package pt.org.aguiaj.classes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.widgets.IconWidget;
import pt.org.aguiaj.common.widgets.LabelWidget;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.standard.StandardNamePolicy;

public class AbstractClassWidget extends Composite {

	public AbstractClassWidget(Composite parent, Class<?> clazz) {
		super(parent, SWT.BORDER);
		setLayout(new RowLayout(SWT.HORIZONTAL));
		new IconWidget(this, clazz).setToolTipText("Polymorphic type");
		LabelWidget classNameLabel =  new LabelWidget.Builder()
			.text(StandardNamePolicy.prettyClassName(clazz))
			.big()
			.create(this);
		
		DocumentationView.getInstance().addDocumentationSupport(classNameLabel.getControl(), clazz);
//		ClassModel.getInstance().addClass(clazz);
	}
}
