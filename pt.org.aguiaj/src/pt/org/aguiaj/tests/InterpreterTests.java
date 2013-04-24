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
package pt.org.aguiaj.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import pt.org.aguiaj.core.commands.java.JavaCommandWithReturn;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.Parser;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.Reference;

public class InterpreterTests {

	private String[] validAssignments = {
			"String s;",
			"String s = \"mimi\";",
			"String s = a.substring(1);",
			"String s = a.concat(b);",
			"a=b;"
	};

//	private String[] invalidTestLines = {
//
//	};



	private static Map<String, Reference> referenceTable;
	private static Set<Class<?>> classSet;

	static {
		referenceTable = new HashMap<String, Reference>();
		referenceTable.put("a", new Reference("a", String.class, "AAA"));
		referenceTable.put("b", new Reference("b", String.class, "BBB"));
		referenceTable.put("i", new Reference("i", int.class, new Integer(1)));
		referenceTable.put("j", new Reference("j", Integer.class, new Integer(5)));
		referenceTable.put("z", new Reference("z", Double.class, new Double(1.4)));
		
		classSet = new HashSet<Class<?>>();
		classSet.add(String.class);
		classSet.add(Integer.class);
		classSet.add(Double.class);
	}
 
	private enum TestMethodExpression {
		ON_LITERAL("\"uma string_\".concat(\"toto\")", "uma string_toto"),
		ON_REFERENCE1("a.concat(b)", "AAABBB"),
		ON_REFERENCE2("a.substring(0,3)", "AAA"),
		
		ON_NEWOBJECT("new String(\"A\").concat(new String(\"B\"))", "AB"),
		
		BOOLEAN_TRUE("\"\".isEmpty()", true),
		BOOLEAN_FALSE("a.isEmpty()", false),
		
		CHARACTER("a.charAt(1)", 'A'),
		
		INTEGER1("j.intValue()", 5),
		INTEGER2("new Integer(10).toString().concat(\"00\")", "1000"),
		
		DOUBLE1("z.toString()", "1.4"),
		DOUBLE2("new Double(1.2)", 1.2),
		DOUBLE3("new Double(1.3).toString()", "1.3"),
		
		COMPOSITION1("a.concat(a.substring(1))", "AAAAA"),
		COMPOSITION2("a.concat(a.substring(i))", "AAAAA"),
		COMPOSITION3("a.concat(a.substring(0,2)).length()", 5),
		COMPOSITION4("b.concat(new String(\"???\"))", "BBB???"),
		
		STATIC("Integer.parseInt(\"7\")", 7),
		
		ARRAY_PRIMITIVE_1D("new int[3]", new int[] {0, 0, 0});

		public final String line;
		public final Object expected;

		private TestMethodExpression(String line, Object expected) {
			this.line = line;
			this.expected = expected;
		}
	}

	@Test
	public void testExpression() {
		testMethodExpression(TestMethodExpression.ARRAY_PRIMITIVE_1D);
	}


	@Test
	public void testExpressions() {
		for(TestMethodExpression exp : TestMethodExpression.values()) {
			testMethodExpression(exp);
		}
	}

	private void testMethodExpression(TestMethodExpression exp) {
		Instruction inst = Parser.accept(exp.line, referenceTable, classSet);
		String testCase = exp.toString() + " : " + exp.line;
		assertNotNull(testCase, inst);
		JavaCommand cmd = inst.getCommand();
		assertTrue(cmd instanceof JavaCommandWithReturn);
		JavaCommandWithReturn jcmd = (JavaCommandWithReturn) cmd;
		jcmd.execute();			
		
		if(exp.expected.getClass().isArray()) {
			Object obj = jcmd.getResultingObject();
			assertNotNull(obj);
			assertTrue(testCase, obj.getClass().isArray());
			assertTrue(testCase, Arrays.deepEquals((Object[]) exp.expected, (Object[]) jcmd.getResultingObject()));
		}
		else
			assertEquals(testCase, exp.expected, jcmd.getResultingObject());
		
	}
	

	//	@Test
	public void testProb() {
		assertNotNull(validAssignments[10], Parser.accept(validAssignments[10], referenceTable, classSet));
	}

	@Test
	public void testAcceptance() {		
		for(String line : validAssignments)		
			assertNotNull(line, Parser.accept(line, referenceTable, classSet));
	}

}
