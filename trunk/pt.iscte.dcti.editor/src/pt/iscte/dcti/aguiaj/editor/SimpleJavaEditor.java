package pt.iscte.dcti.aguiaj.editor;


import org.eclipse.ui.editors.text.TextEditor;



public class SimpleJavaEditor extends TextEditor {

	public SimpleJavaEditor() {
		setSourceViewerConfiguration(new Configuration());
		setDocumentProvider(new JavaClassProvider());
	}
}
