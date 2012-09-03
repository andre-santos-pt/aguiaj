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
package pt.org.aguiaj.aspects;

public aspect PolicyEnforcement {

	declare warning
	: get(java.io.PrintStream System.out) &&
	!within(pt.aguiaj.debug..*)
	: "illegal access to System.out";

	declare warning
	: get(java.io.PrintStream System.err) &&
	!within(pt.aguiaj.debug..*)
	: "review access to System.err";
}
