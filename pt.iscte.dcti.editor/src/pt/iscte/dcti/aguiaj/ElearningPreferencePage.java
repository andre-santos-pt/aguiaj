/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package pt.iscte.dcti.aguiaj;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ElearningPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	

	@Override
	public void init(IWorkbench workbench) {

	}

	
	@Override
	protected void createFieldEditors() {
		StringFieldEditor user = new StringFieldEditor("Username", "Username", getFieldEditorParent());
		addField(user);
	}

}
