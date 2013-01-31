/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package pt.org.aguiaj.core.interpreter;

import java.util.Map;
import java.util.Set;

import pt.org.aguiaj.common.Reference;

public enum ExpressionMatcher {
	INT(IntLiteral.class),
	DOUBLE(DoubleLiteral.class),
	BOOLEAN(BooleanLiteral.class),
	CHAR(CharacterLiteral.class),
	
	
	INT_ARRAY(IntArray.class),
	DOUBLE_ARRAY(DoubleArray.class),
	BOOLEAN_ARRAY(BooleanArray.class),
	CHAR_ARRAY(CharArray.class),
	
	INT_ARRAY_2D(IntArray2DLiteral.class),
	
	NULL(Null.class),
	STRING(StringLiteral.class),
	
	REFERENCE(ExistingReference.class),
	
	ATTRIBUTE(Attribute.class),
	
	ENUM_CONST(EnumConstant.class),
	
	ARRAY_POSITION(ArrayPosition.class),
	
	METHOD_CALL(MethodCall.class),
	
	CONSTRUCTOR_CALL(ConstructorCall.class),
	ARRAY_CREATION(ArrayCreation.class);
	
	
	private Class<? extends Expression> expressionClass;
	
	private ExpressionMatcher(Class<? extends Expression> expressionClass) {
		this.expressionClass = expressionClass;
	}
	
	public static Expression match(
			String text, 
			Map<String, Reference> referenceTable, 
			Set<Class<?>> classSet) {
		
		for(ExpressionMatcher matcher : values()) {
			try {
				Expression exp = (Expression) matcher.expressionClass.newInstance();
				if(exp.acceptText(text, referenceTable, classSet))
					return exp;
				
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
