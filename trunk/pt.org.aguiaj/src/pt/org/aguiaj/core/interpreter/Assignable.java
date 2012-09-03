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
package pt.org.aguiaj.core.interpreter;

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;

public interface Assignable {
	Class<?> type();
	String getExpressionText();
	boolean canAssign();
	boolean acceptText(String text, 
			Map<String, Reference> referenceTable, 
			Set<Class<?>> classSet)
			throws ParseException;
}
