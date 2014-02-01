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
package pt.org.aguiaj.core;

import java.util.Map;
import java.util.regex.Matcher;

public enum UIText {
	SHOW_OPERATIONS,
	HIDE_OPERATIONS,
	SHOW_PRIVATE_FIELDS,
	HIDE_PRIVATE_FIELDS,
	OBJECT_OF_TYPE("Object of type %1"),
	IS_A("%1 is a %2"),
	GENERATE_PLUGIN, 
	CLOSE, REMOVE,
	TRANSLATION,
	ORIGINAL_MESSAGE, 
	NEW_LANGUAGE_PLUGIN, 
	NEW_LANGUAGE_FEATURE("This feature enables you to create support for a new language for AGUIA/J. " +
			"After filling out the table and pressing the button, you generate an Eclipse plugin. " +
			"The entries left blank will remain with the original English message."), 
	LANGUAGE_NAME,
	AUTHOR, RETURN_VALUE, SYNTAX_ERROR, 
	RUNTIME_ERROR("Runtime error: %1."),
	TOO_LONG_TIME("Too long time executing..."),
	INFINITE_CYCLE_AT("Infinite cycle at %1?"), 
	OUT_OF_MEMORY, 
	STACK_OVERFLOW,
	COMPILATION_ERRORS,
	CHECK_METHOD_RECURSION("Check method %1 for recursive method calls."),
	CHECK_METHOD_MEMORY("Check method %1 for excessive memory usage."), 
	CHECK_CLASS_AT("Check class %1 on line %2. %3"), 
	SHOW_PROPERTIES, HIDE_PROPERTIES, SHOW_FIELDS, HIDE_FIELDS,
	STATIC_METHODS, STATIC_FIELDS, ENUM_CONSTANTS, CONSTRUCTORS, FIELDS, PROPERTIES, OPERATIONS;
	
	private String defaultValue;
	private String value;
	
	private UIText() {
		defaultValue = getDefaultName();
		value = defaultValue;
	}
	
	private UIText(String defaultValue) {
		this.defaultValue = defaultValue;
		value = defaultValue;
	}
	
	private String getDefaultName() {
		String ret = name().replace('_', ' ');
		return Character.toUpperCase(ret.charAt(0)) + ret.substring(1).toLowerCase();
	}
	
	public static void update(Map<String, String> table) {
		for(UIText text : values()) {
			if(table.containsKey(text.name()))
				text.value = table.get(text.name());
		}
	}
	
	public String get(Object ... args) {
		String ret = value;
		int i = 1;
		for(Object arg : args) {
			ret = ret.replaceFirst("%"+i, arg == null ? "" : Matcher.quoteReplacement(arg.toString().replace('$', '.')));
			i++;
		}
		return ret;
	}
}
