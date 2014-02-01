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
package pt.iscte.dcti.aguiaj.editor;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;


public class JavaCodePartitionScanner extends RuleBasedPartitionScanner {
	public final static String JAVA_COMMENT = "__java_comment";
//	public final static String JAVA_CODE = "__java_code";

	public JavaCodePartitionScanner() {

		IToken javaComment = new Token(JAVA_COMMENT);
//		IToken javaCode = new Token(JAVA_CODE);

		IPredicateRule[] rules = new IPredicateRule[2];

		rules[0] = new MultiLineRule("/*", "*/", javaComment);
		rules[1] = new EndOfLineRule("//", javaComment);
		
//		rules[1] = new TagRule(javaCode);

		setPredicateRules(rules);
	}
}
