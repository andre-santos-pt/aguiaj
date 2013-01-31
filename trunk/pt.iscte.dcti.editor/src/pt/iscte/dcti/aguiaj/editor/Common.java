package pt.iscte.dcti.aguiaj.editor;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;

public class Common {
	public static IToken createToken(TokenColor color, boolean bold) {
		if(bold)
			return new Token(new TextAttribute(color.color, null, SWT.BOLD));
		else
			return new Token(new TextAttribute(color.color));

	}
}

