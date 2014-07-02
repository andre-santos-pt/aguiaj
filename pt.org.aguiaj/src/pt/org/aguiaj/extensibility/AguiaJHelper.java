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
package pt.org.aguiaj.extensibility;

import java.util.Collection;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import pt.org.aguiaj.classes.ClassesView;
import pt.org.aguiaj.common.AguiaJImage;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.ReflectionUtils;
import pt.org.aguiaj.core.documentation.DocumentationView;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.ParseException;
import pt.org.aguiaj.core.interpreter.Parser;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import pt.org.aguiaj.extensibility.contracts.ContractUtil;
import pt.org.aguiaj.objects.ObjectModel;
import pt.org.aguiaj.objects.ObjectWidget;
import pt.org.aguiaj.objects.ObjectsView;

/**
 * Helper class to interact with AguiaJ.
 */
public class AguiaJHelper {
	public static String getWorkingDirectory() {
		return AguiaJActivator.getInstance().getWorkingDirectory().toOSString();
	}
	
	public static Image getPluginImage(String key) {
		Image img = AguiaJActivator.getInstance().getImageRegistry().get(key);
		if(img == null) {
			img = AguiaJImage.NA.getImage();
		}
		
		return img;
	}
	
	public static String getActivePlugin() {
		return ClassesView.getInstance().getActivePlugin();
	}
	
	
	public static String getPluginJarLocation(String pluginId) {
		return AguiaJActivator.getInstance().getPluginJarLocation(pluginId);
	}
	
	public static Set<String> getPluginIds() {
		return AguiaJActivator.getInstance().getPluginIds();
	}
	
	public static Set<String> getPluginPackages() {
		return AguiaJActivator.getInstance().getPluginPackages();
	}
	
	public static Collection<Class<?>> getAllPluginClasses() {
		return AguiaJActivator.getInstance().getAllPluginClasses();
	}
	
	public static void showInDocumentation(String url) {
		DocumentationView.getInstance().load(url);
	}
	
	public static void addExceptionListener(ExceptionListener l) {
		ExceptionHandler.INSTANCE.addListener(l);
	}
	
	
	public static void updateObject(Object object) {
//		if(object instanceof ContractDecorator<?>)
//			object = ((ContractDecorator<?>) object).getWrappedObject();
		
		ObjectWidget widget = ObjectsView.getInstance().getObjectWidget(ContractUtil.unwrap(object));
		widget.updateFields();
		ObjectsView.getInstance().updateLayout(null);
	}
	
	public static void executeJavaInstruction(String javaInstruction) {
		try {
			Instruction instruction = Parser.accept(javaInstruction);
			
			if(instruction != null) {
				ObjectModel.getInstance().execute(instruction.getCommand());
			}
		}
		catch(ParseException e) {
			throw new IllegalArgumentException("Invalid Java instruction - " + javaInstruction);
		}
	}
	
	public static boolean existsObject(Object object) {
		return ObjectModel.getInstance().existsObject(object);
	}
	
	public static boolean existsReference(String name) {
		return ObjectModel.getInstance().existsReference(name);
	}
	
	public static void removeReference(String name) {
		if(name == null)
			throw new NullPointerException("Reference name cannot be null");
		
		ObjectModel.getInstance().removeReference(name);
	}

	public static void show(String objectReference) {
		ObjectsView.getInstance().show(objectReference);
	}
	
	public static void hide(String objectReference) {
		ObjectsView.getInstance().hide(objectReference);
	}
	
	public static String getTextualRepresentation(Object o) {
		return ReflectionUtils.getTextualRepresentation(o, true);
	}
	
//	public static void addObject(Object object) {
//		if(object == null)
//			throw new NullPointerException("Object cannot be null");
//		
//		Class<?> clazz = object.getClass();
//		NewReferenceCommand cmd = new NewReferenceCommand(clazz, object, null);
//		ObjectModel.getInstance().execute(cmd);
//	}
	
}
