package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.eclipselabs.javainterpreter.JavaInterpreter;
import org.eclipselabs.javainterpreter.Output;
import org.junit.Before;
import org.junit.Test;

public class Tests {

	private JavaInterpreter interpreter;
	
	private static final String[][] testCases = {
		{"abs(-2)", Output.get(2)},	
		{"abs(1.9)", Output.get(1.9)},
		{"round(3.4)", Output.get(3)},
		{"round(4)", Output.get(4)},
		{"max(4,5)", Output.get(5)},
		{"min(3.2,5.5)", Output.get(3.2)},
		
		{"valueOf(true)", Output.get("true")},
		
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
		interpreter = new JavaInterpreter();
		interpreter.addClass(Math.class);
		interpreter.addClass(String.class);
		interpreter.addClass(Integer.class);
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
