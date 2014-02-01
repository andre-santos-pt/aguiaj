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
package pt.org.aguiaj.core.interpreter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.extensibility.JavaCommand;

public abstract class ArrayLiteral extends Literal implements Instruction {
	private String text;

	protected ArrayLiteral(Class<?> type) {
		super(type);		
	}

	private static String[] parseArrayElements(String s) {
		List<Integer> commas = new ArrayList<Integer>();

		boolean open = false;
		for(int i = 0; i < s.length(); i++) {
			if(s.charAt(i) == '{')
				open = true;
			if(s.charAt(i) == '}')
				open = false;

			if(s.charAt(i)==',' && !open)
				commas.add(i);
		}

		String[] parts = new String[commas.size()+1];
		Iterator<Integer> commasIt = commas.iterator();
		int last = 0;
		for(int i = 0; i < parts.length; i++) {
			if(commasIt.hasNext()) {
				int c = commasIt.next();			
				parts[i] = s.substring(last, c).trim();
				last = c + 1;
			}
			else
				parts[i] = s.substring(last).trim();
		}
		return parts;
	}

	@Override
	public final boolean accept(String text) {
		this.text = text;		
		String line = text.trim();
		if(!line.startsWith("{") || !line.endsWith("}"))
			return false;

		line = line.substring(1, line.length() - 1);
		
		String[] parts = line.indexOf('{') != -1 ? parseArrayElements(line) : line.split("\\s*,\\s*");

		if(parts.length == 0 || parts[0].isEmpty())
			return false;

		for(int i = 0; i < parts.length; i++) {
			parts[i] = parts[i].trim();
		}

		value = accept(parts);
		return value != null;
	}

	protected abstract Object accept(String[] parts);

	public JavaCommand getCommand() {
		return new NewReferenceCommand(type(), value, text); 
	}
}
