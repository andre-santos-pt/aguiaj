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

public enum InstructionMatcher {
	STRING_LITERAL(StringLiteral.class),
	ENUM_CONSTANT(EnumConstant.class),
	
	REFERENCE_DECLARATION_AND_ASSIGN(ReferenceDeclarationAndAssignment.class),	
	REFERENCE_ASSIGN(ReferenceAssignment.class),
	REFERENCE_DECLARATION(ReferenceDeclaration.class),
	
	ARRAYPOSITION_ASSIGN(ArrayPositionAssignment.class),
	ATTRIBUTE_ASSIGN(AttributeAssignment.class),

	METHOD_CALL(MethodCall.class),
	CONSTRUCTOR_CALL(ConstructorCall.class),	
	ARRAYCREATION_CALL(ArrayCreation.class),
	
	INT_ARRAY(IntArray.class),
	DOUBLE_ARRAY(DoubleArray.class),
	BOOLEAN_ARRAY(BooleanArray.class),
	CHAR_ARRAY(CharArray.class),
	
	IMPORT(ImportInstruction.class);


	private Class<? extends Instruction> instructionClass;

	private InstructionMatcher(Class<? extends Instruction> instruction) {
		this.instructionClass = instruction;
	}

	public static Instruction match(String text, Map<String, Reference> referenceTable, Set<Class<?>> classSet) {
		for(InstructionMatcher matcher : values()) {
			try {
				Instruction instruction = matcher.instructionClass.newInstance();
				if(instruction.acceptText(text, referenceTable, classSet))
					return instruction;
			} 
			catch (InstantiationException e) {
				e.printStackTrace();
			} 
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
