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


import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;

public class JavaInterpreter {

	private ExpressionVisitor visitor;
	
	
	public JavaInterpreter(Context context) {
		visitor = new ExpressionVisitor(context);
	}
	
	
//	public void setMainClass(Class<?> c) {
//		visitor.setMainClass(c);
//	}
//	
//	public void addClass(Class<?> c) {
//		visitor.addBaseClass(c);
//	}
//	
//	public void addReference(Class<?> type, String name, Object object) {
//		visitor.addReference(type, name, object);
//	}
//	
//	public void clear() {
//		visitor.clear();
//	}
	
//	public void evaluateStatement(String statement) {
//		ASTParser parser = ASTParser.newParser(AST.JLS4); 
//		parser.setKind(ASTParser.K_STATEMENTS);
//		parser.setResolveBindings(true);
//		parser.setStatementsRecovery(false);
//		parser.setSource(statement.toCharArray());
//		ASTNode node = parser.createAST(null);
//
//		if(node.getNodeType() == ASTNode.BLOCK) {
//			Block block = (Block) node;
//			List statements = (List) block.getStructuralProperty(Block.STATEMENTS_PROPERTY);
//			for(Object stat : statements) {
//				System.out.println( ": " + stat.getClass());
//				if(stat instanceof ASTNode)
//					((ASTNode) stat).accept(new AssignmentVisitor());
//				
//			}
//			
//		}
//	}
	
	
	public Object evaluateMethodInvocation(String expression) {
		ASTParser parser = ASTParser.newParser(AST.JLS4); 
		parser.setKind(ASTParser.K_EXPRESSION);
		parser.setResolveBindings(true);
		parser.setStatementsRecovery(false);
		parser.setSource(expression.toCharArray());
		ASTNode node = parser.createAST(null);
	
		if((node.getFlags() & ASTNode.MALFORMED) == ASTNode.MALFORMED)
			throw new IllegalArgumentException("Parse error");
		
		if(node.getNodeType() == ASTNode.METHOD_INVOCATION || 
		   node.getNodeType() == ASTNode.ASSIGNMENT || 
		   node.getNodeType() == ASTNode.CLASS_INSTANCE_CREATION ||
		   node.getNodeType() == ASTNode.ARRAY_CREATION ||	   
		   node.getNodeType() == ASTNode.STRING_LITERAL) {
			
			try {
				node.accept(visitor);
			}
			catch(RuntimeException ex) {
				if(ex.getCause() != null)
					ex.printStackTrace();
				
				throw new IllegalArgumentException(ex.getMessage());
			}
			
			return visitor.resolve();
		}
//		else if(node.getNodeType() == ASTNode.ASSIGNMENT) {
//			AssignmentVisitor visitor = new AssignmentVisitor();
//			try {
//				node.accept(visitor);
//			}
//			catch(RuntimeException ex) {
//				throw ex;
//			}
//			variables.put(visitor.variable, visitor.result);
//			return visitor.result;
//		}
		throw new IllegalArgumentException("Parse error");
	}

//	class AssignmentVisitor extends ASTVisitor {
//		String variable;
//		Object result;
//		
//		@Override
//		public boolean visit(Assignment ass) {
//			Expression left = ass.getLeftHandSide();
//			Expression right = ass.getRightHandSide();
//			
//			if(left instanceof SimpleName) {
//				try {
//					right.accept(visitor);
//				}
//				catch(RuntimeException ex) {
//					ex.printStackTrace();
//					throw ex;
//				}
//				
//				variable = ((SimpleName) left).getIdentifier();
//				result = visitor.resolve();
//			}
//			return true;
//		}
		
//		@Override
//		public boolean visit(ExpressionStatement node) {
//			if(node.getExpression() instanceof Assignment) {
//				Assignment ass = (Assignment) node.getExpression();
//				Expression left = ass.getLeftHandSide();
//				Expression right = ass.getRightHandSide();
//				
//				if(left instanceof SimpleName) {
//					try {
//						right.accept(visitor);
//					}
//					catch(RuntimeException ex) {
//						throw ex;
//					}
//					variables.put(((SimpleName) left).getIdentifier(), visitor.resolve());
//				}
//				System.out.println("LEFT: " + left.getClass());
//				System.out.println("RIGHT: " + right.getClass());
//			}
//			return true;
//		}
//	}

	
}
