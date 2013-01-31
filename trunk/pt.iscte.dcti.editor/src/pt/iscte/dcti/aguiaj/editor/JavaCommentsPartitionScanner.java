package pt.iscte.dcti.aguiaj.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.WordRule;

public class JavaCommentsPartitionScanner extends RuleBasedPartitionScanner  {

	/**
	 * Detector for empty comments.
	 */
	static class EmptyCommentDetector implements IWordDetector {

		/*
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return (c == '/');
		}

		/*
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return (c == '*' || c == '/');
		}
	}


	/**
	 * Word rule for empty comments.
	 */
	static class EmptyCommentRule extends WordRule implements IPredicateRule {

		private IToken fSuccessToken;
		/**
		 * Constructor for EmptyCommentRule.
		 * @param successToken
		 */
		public EmptyCommentRule(IToken successToken) {
			super(new EmptyCommentDetector());
			fSuccessToken= successToken;
			addWord("/**/", fSuccessToken); //$NON-NLS-1$
		}

		/*
		 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
		 */
		public IToken evaluate(ICharacterScanner scanner, boolean resume) {
			return evaluate(scanner);
		}

		/*
		 * @see IPredicateRule#getSuccessToken()
		 */
		public IToken getSuccessToken() {
			return fSuccessToken;
		}
	}



	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 */
	public JavaCommentsPartitionScanner() {
		super();

//		IToken string= Common.createToken(TokenColor.STRING, false);
//		IToken character= Common.createToken(TokenColor.CHAR, false);
		IToken javaDoc= Common.createToken(TokenColor.COMMENT, true);
		IToken multiLineComment= Common.createToken(TokenColor.COMMENT, false);
		IToken singleLineComment= Common.createToken(TokenColor.COMMENT, false);

		List<IRule> rules= new ArrayList<IRule>();

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", singleLineComment)); //$NON-NLS-1$

		// Add rule for strings.
//		rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

		// Add rule for character constants.
//		rules.add(new SingleLineRule("'", "'", character, '\\')); //$NON-NLS-2$ //$NON-NLS-1$

		// Add special case word rule.
		EmptyCommentRule wordRule= new EmptyCommentRule(multiLineComment);
		rules.add(wordRule);

		// Add rules for multi-line comments and javadoc.
		rules.add(new MultiLineRule("/**", "*/", javaDoc)); //$NON-NLS-1$ //$NON-NLS-2$
		rules.add(new MultiLineRule("/*", "*/", multiLineComment)); //$NON-NLS-1$ //$NON-NLS-2$

		IPredicateRule[] result= new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}