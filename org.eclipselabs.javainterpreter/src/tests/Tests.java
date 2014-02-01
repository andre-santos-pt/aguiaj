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
package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.eclipselabs.javainterpreter.JavaInterpreter;
import org.eclipselabs.javainterpreter.Output;
import org.eclipselabs.javainterpreter.SimpleContext;
import org.junit.Before;
import org.junit.Test;

public class Tests {

	private JavaInterpreter interpreter;
	
	private static final String[][] testCases = {
		
		
		{"Math.round(3.4)", Output.get(3)},
		{"Math.abs(-2)", Output.get(2)},	
		{"Math.abs(1.9)", Output.get(1.9)},
		
		{"Math.round(4)", Output.get(4)},
		{"Math.max(4,5)", Output.get(5)},
		{"Math.min(3.2,5.5)", Output.get(3.2)},
		
		{"String.valueOf(true)", Output.get("true")},
		
		{"parseInt(\"-8\")", Output.get(-8)},
		{"compare(4,5)", Output.get(-1)},
		{"toBinaryString(31)", Output.get("11111")},
		
		// arrays
		{"v = new int[3]", Output.get(new int[3])},
		
		{"v2 = new int[]{1,2,3}", Output.get(new int[] {1,2,3})},
		
		// function in function
		
		
		{"s = \"tiit\"",  Output.get("tiit")},
		{"n = \"5\"",  Output.get("5")},
		{"parseInt(n)",  Output.get(5)}
	};

	@Before
	public void setup() {
		SimpleContext context = new SimpleContext(Integer.class, Math.class, String.class);
		context.addReference(String.class, "s", "MyString");
		context.addReference(Integer.class, "i", null);
		interpreter = new JavaInterpreter(context);
	}
	
	@Test
	public void testPrimitive() {
		for(String[] test : testCases)
			try {
				assertEquals(Arrays.toString(test), test[1], Output.get(interpreter.evaluateMethodInvocation(test[0])));
			}
			catch(RuntimeException ex) {
				fail(Arrays.toString(test) + " - " + ex.getMessage());
				ex.printStackTrace();
			}
	}

}
