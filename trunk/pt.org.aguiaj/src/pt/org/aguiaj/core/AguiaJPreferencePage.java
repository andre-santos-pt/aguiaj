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

import static pt.org.aguiaj.core.AguiaJParam.ACCESSOR_POLICY;
import static pt.org.aguiaj.core.AguiaJParam.JAVABAR_FONT;
import static pt.org.aguiaj.core.AguiaJParam.METHOD_TIMEOUT;

import java.util.Set;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import pt.org.aguiaj.core.AguiaJParam.PreferencesParam;

public class AguiaJPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		IPreferenceStore store = AguiaJActivator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	protected void createFieldEditors() {
					
		ScaleFieldEditor fontSize = new ScaleFieldEditor(PreferencesParam.FONTSIZE.name(), PreferencesParam.FONTSIZE.description, getFieldEditorParent()) {{			
			setMinimum(10);
			setMaximum(20);
			setIncrement(1);
		}};
		addField(fontSize);
		
		
		String[][] fontFaceValues = {
				{"Verdana", "Verdana"},
				{"Arial", "Arial"},				
				{"Courier", "Courier"},
				{"Times", "Times"}
		};
		ComboFieldEditor fontFace = new ComboFieldEditor(PreferencesParam.FONTFACE.name(), PreferencesParam.FONTFACE.description, fontFaceValues, getFieldEditorParent());
		addField(fontFace);
		
		IntegerFieldEditor methodTimeOut = new IntegerFieldEditor(METHOD_TIMEOUT.name(), "Method timeout", getFieldEditorParent()) {{
			setValidRange(1, 10);			
		}};
		
		IntegerFieldEditor javaBarFontSize = new IntegerFieldEditor(JAVABAR_FONT.name(), "Java Bar font size", getFieldEditorParent()) {{
			setValidRange(10, 32);
		}};
		addField(javaBarFontSize);
		
		addField(methodTimeOut);
		
		addAccessorPolicies();
//		addEncapsulationPolicy();
	}

	private void addAccessorPolicies() {
		Set<String> accessorPolicies = 
			AguiaJActivator.getDefault().getAccessorPolicies();
		String[][] inspectorValues = new String[accessorPolicies.size()][2];
		int i = 0;
		for(String policy : accessorPolicies) {
			inspectorValues[i][0] = policy;
			inspectorValues[i][1] = policy;
			i++;
		}
		RadioGroupFieldEditor accessorOption = new RadioGroupFieldEditor(
				ACCESSOR_POLICY.name(),
				"Accessor method detection policy",
				1,
				inspectorValues,
				getFieldEditorParent(),
				true);
		addField(accessorOption);
	}

//	private void addEncapsulationPolicy() {
//		BooleanFieldEditor protectedVisible = new BooleanFieldEditor(PROTECTED_VISIBLE.name(), "Protected members are visible", getFieldEditorParent());
//		addField(protectedVisible);
//		BooleanFieldEditor packagedefVisible = new BooleanFieldEditor(PACKAGEDEF_VISIBLE.name(), "Package default members are visible", getFieldEditorParent());
//		addField(packagedefVisible);
//		
//	}
}
