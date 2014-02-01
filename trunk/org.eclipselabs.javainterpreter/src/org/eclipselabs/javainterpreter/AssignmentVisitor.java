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
package org.eclipselabs.javainterpreter;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class AssignmentVisitor extends ASTVisitor {

	@Override
	public boolean visit(VariableDeclarationStatement node) {
		System.out.println("STAT: " + node);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		System.out.println("FRAG: " + node);
		return true;
	}
	
	@Override
	public boolean visit(VariableDeclarationExpression node) {
		System.out.println("DEC: " + node);
		return true;
	}
	

	@Override
	public boolean visit(ExpressionStatement node) {
		if(node.getExpression() instanceof Assignment) {
			Assignment ass = (Assignment) node.getExpression();
			Expression left = ass.getLeftHandSide();
			Expression right = ass.getRightHandSide();
			System.out.println("LEFT: " + left.getClass());
			System.out.println("RIGHT: " + right.getClass());
		}
		return true;
	}
}
