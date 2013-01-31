package pt.iscte.dcti.aguiaj.editor;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class IntRule implements IRule {

	/** Internal setting for the un-initialized column constraint */
	protected static final int UNDEFINED= -1;
	/** The token to be returned when this rule is successful */
	protected IToken fToken;
	/** The column constraint */
	protected int fColumn= UNDEFINED;

	/**
	 * Creates a rule which will return the specified
	 * token when a numerical sequence is detected.
	 *
	 * @param token the token to be returned
	 */
	public IntRule(IToken token) {
		Assert.isNotNull(token);
		fToken= token;
	}

	/**
	 * Sets a column constraint for this rule. If set, the rule's token
	 * will only be returned if the pattern is detected starting at the
	 * specified column. If the column is smaller then 0, the column
	 * constraint is considered removed.
	 *
	 * @param column the column in which the pattern starts
	 */
	public void setColumnConstraint(int column) {
		if (column < 0)
			column= UNDEFINED;
		fColumn= column;
	}

	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int first = scanner.read();
		int c = first;
		int i = 1;
		if (Character.isDigit(c) || c == '-') {
			if (fColumn == UNDEFINED || (fColumn == scanner.getColumn() - 1)) {
				do {
					c= scanner.read();
					if(i == 1 && first == '-' && !Character.isDigit(c)) {
						scanner.unread();
						scanner.unread();
						return Token.UNDEFINED;
					}
					i++;
				} while (Character.isDigit(c));
				scanner.unread();
				return fToken;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
}