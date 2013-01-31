package pt.iscte.dcti.aguiaj.editor;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;


public class JavaCodeScanner extends RuleBasedScanner {
	private static IToken comment = Common.createToken(TokenColor.COMMENT, false);
	private static IToken javadoc = Common.createToken(TokenColor.COMMENT, true);
	private static IToken string = Common.createToken(TokenColor.STRING, false);
	private static IToken character = Common.createToken(TokenColor.CHAR, false);
	private static IToken keyword = Common.createToken(TokenColor.KEYWORD, true);
	private static IToken number =  Common.createToken(TokenColor.NUMBER, true);
	private static IToken id =  Common.createToken(TokenColor.ID, true);
	private static IToken trueLit =  Common.createToken(TokenColor.TRUE, true);
	private static IToken falseLit =  Common.createToken(TokenColor.FALSE, true);
	private static IToken defaultToken = Common.createToken(TokenColor.BLACK, false);

	public JavaCodeScanner() {
		IRule[] rules = new IRule[] {

				//				new TrueWordRule(),
				//				new EndOfLineRule("//", comment),
				//				new SingleLineRule("\\\\", "", comment),
				new SingleLineRule("\"", "\"", string, '\\'),
				new SingleLineRule("'", "'", character, '\\'),
				new SingleLineRule("tr", "ue", trueLit, '\\'),
				new SingleLineRule("fal", "se", falseLit, '\\'),
				new KeyWordRule(),
				//				new MultiLineRule("/**", "*/", javadoc),
				//				new MultiLineRule("/*", "*/", comment),

				new IntRule(number),
				new WhitespaceRule(new IWhitespaceDetector() {
					public boolean isWhitespace(char c) {
						return Character.isWhitespace(c);
					}
				})
		};
		setRules(rules);
	}

	private final static String[] javaKeyword =
	{ "abstract", "continue", "for", "new", "switch", "assert", "default",
		"goto",	"package", "synchronized", "boolean", "do", "if", "private",
		"this", "break", "double",	"implements", "protected", "throw",
		"byte",	"else",	"import",	"public",	"throws", "case", "enum",
		"instanceof", "return",	"transient", "catch", "extends", "int",	
		"short", "try", "char",	"final", "interface", "static",	"void",
		"class", "finally",	"long",	"strictfp",	"volatile", "const", "float",
		"native", "super",	"while"};




	private static class KeyWordRule extends WordRule {
		public KeyWordRule() {
			super(new IWordDetector() {
				@Override
				public boolean isWordStart(char c) {
					return Character.isJavaIdentifierStart(c);
				}

				@Override
				public boolean isWordPart(char c) {
					return Character.isJavaIdentifierPart(c);
				}}, defaultToken);

			for(String word : javaKeyword)
				addWord(word, keyword);
		}
	}

	private static class TrueWordRule extends WordRule {
		public TrueWordRule() {
			super(new IWordDetector() {
				@Override
				public boolean isWordStart(char c) {
					return Character.isJavaIdentifierStart(c);
				}

				@Override
				public boolean isWordPart(char c) {
					return Character.isJavaIdentifierPart(c);
				}}, defaultToken);

			addWord("true", trueLit);
		}
	}

}
