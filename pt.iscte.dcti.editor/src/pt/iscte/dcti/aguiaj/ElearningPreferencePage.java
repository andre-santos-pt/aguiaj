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