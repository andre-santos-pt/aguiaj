package pt.iscte.dcti.aguiaj.editor;

import org.eclipse.jface.text.rules.IWordDetector;

public class IntLiteralDetector implements IWordDetector {
	
	public boolean isWordStart(char c) {
		return Character.isDigit(c) || c == '-';
	}

	public boolean isWordPart(char c) {
		return Character.isDigit(c);
	}
}