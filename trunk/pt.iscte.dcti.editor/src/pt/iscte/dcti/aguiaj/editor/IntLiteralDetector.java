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

import org.eclipse.jface.text.rules.IWordDetector;

public class IntLiteralDetector implements IWordDetector {
	
	public boolean isWordStart(char c) {
		return Character.isDigit(c) || c == '-';
	}

	public boolean isWordPart(char c) {
		return Character.isDigit(c);
	}
}
