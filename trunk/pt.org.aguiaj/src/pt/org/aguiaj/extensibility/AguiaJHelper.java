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
package pt.org.aguiaj.extensibility;

import org.eclipse.swt.graphics.Image;

import pt.org.aguiaj.classes.ClassesView;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.DocumentationView;
import pt.org.aguiaj.core.commands.java.NewReferenceCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.ParseException;
import pt.org.aguiaj.core.interpreter.Parser;
import pt.org.aguiaj.objects.ObjectWidget;
import pt.org.aguiaj.objects.ObjectsView;

/**
 * Helper class to interact with AGUIA/J.
 */
public class AguiaJHelper {
	public static String getWorkingDirectory() {
		return AguiaJActivator.getDefault().getWorkingDirectory().toOSString();
	}
	
	public static Image getPluginImage(String key) {
		Image img = AguiaJActivator.getDefault().getImageRegistry().get(key);
		if(img == null) {
			img = AguiaJImage.NA.getImage();
		}
		
		return img;
	}
	
	public static String getActivePlugin() {
		return ClassesView.getInstance().getActivePlugin();
	}
	
	
	public static String getPluginJarLocation(String pluginId) {
		return AguiaJActivator.getDefault().getPluginJarLocation(pluginId);
	}
	
	public static void showInDocumentation(String url) {
		DocumentationView.getInstance().load(url);
	}
	
	public static LastException getLastException() {
		return ExceptionHandler.INSTANCE.getLastException();
	}

	
	public static void updateObject(Object object) {
		ObjectWidget widget = ObjectsView.getInstance().getObjectWidget(object);
		widget.updateFields();
		ObjectsView.getInstance().updateLayout(null);
	}
	
	public static void executeJavaInstruction(String javaInstruction) {
		try {
			Instruction instruction = Parser.accept(javaInstruction);
			
			if(instruction != null)
				instruction.getCommand().execute();
		}
		catch(ParseException e) {

		}
	}
	
	public static void addObject(Object object) {
		if(object == null)
			throw new NullPointerException("Object cannot be null");
		
		Class<?> clazz = object.getClass();
		NewReferenceCommand cmd = new NewReferenceCommand(clazz, object, null);
		cmd.execute();
	}
	
}
