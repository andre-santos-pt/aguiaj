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
package pt.org.aguiaj.core.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.classes.ClassesView;
import pt.org.aguiaj.core.AguiaJActivator;
import pt.org.aguiaj.core.commands.java.ConstructorInvocationCommand;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.Parser;
import pt.org.aguiaj.objects.ObjectModel;
import pt.org.aguiaj.objects.ObjectWidget;
import pt.org.aguiaj.objects.ObjectsView;


public class ReloadClassesCommand extends AbstractHandler {
	private IPath workingDir = null;	

	public void setWorkingDir(IPath workingDir) {
		this.workingDir = workingDir;
	}

	private Map<String, EnumSet<ObjectWidget.Section>> expandedSections;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<String> commands = new ArrayList<String>();
		for(JavaCommand c : ObjectModel.getInstance().getActiveCommands()) {
			String instruction = c.getJavaInstruction();
			if(instruction != null)
				commands.add(instruction);
		}
		
		saveExpanded();

		ObjectsView objectsView = ObjectsView.getInstance();
		objectsView.hide();

		ExceptionHandler.INSTANCE.clearErrors();
		ClassModel.getInstance().clearClasses();
		ObjectModel.getInstance().clearAll();

		if(workingDir == null)
			AguiaJActivator.getDefault().reloadClasses();
		else
			AguiaJActivator.getDefault().loadClasses(workingDir);

		Collection<Class<?>> allClasses = AguiaJActivator.getDefault().getPackagesClasses().values();

		for(Class<?> c : allClasses)
			ClassModel.getInstance().addClass(c);

		ClassesView.getInstance().reload(workingDir);				

		ObjectModel.getInstance().addStaticReferences(allClasses);

		List<Class<?>> blackList = new ArrayList<Class<?>>();
		for(String command : commands) {
			try {
				Instruction instruction = Parser.accept(command, 
						ObjectModel.getInstance().getReferenceTable(), 
						ClassModel.getInstance().getAllClasses());

				if(instruction != null) {
					JavaCommand javaCommand = instruction.getCommand(); 

					if(javaCommand instanceof ConstructorInvocationCommand) {
						Class<?> clazz = ((ConstructorInvocationCommand) javaCommand).getConstructor().getDeclaringClass();
						if(blackList.contains(clazz))
							continue;
					}
					else if(javaCommand instanceof MethodInvocationCommand) {
						Method method = ((MethodInvocationCommand) javaCommand).getMethod();
						if(method.getReturnType().equals(void.class) || method.getReturnType().isPrimitive())
							continue;
					}

					ObjectModel.getInstance().execute(javaCommand);

					if(javaCommand instanceof ConstructorInvocationCommand && javaCommand.failed()) {
						blackList.add(((ConstructorInvocationCommand) javaCommand).getConstructor().getDeclaringClass());
					}
				}

			} catch (Exception e) {
				//				System.err.println(e.getMessage() + " -- " + command);
				//				e.printStackTrace();
				//StackTraceElement element = e.getStackTrace()[0];
				//				System.err.println(e.getMessage() + " - " + element.getClassName() + " (line " + element + ")");
			}
		}

		restoreExpanded();
		ClassesView.getInstance().updateClassWidgets();	
		objectsView.show();

		return null;
	}

	private void restoreExpanded() {
		ObjectsView objectsView = ObjectsView.getInstance();

		for(Entry<String, EnumSet<ObjectWidget.Section>> entry : expandedSections.entrySet()) {
			ObjectWidget widget = objectsView.getObjectWidgetByReference(entry.getKey());
			if(widget != null)
				widget.expand(entry.getValue());
		}
	}

	private void saveExpanded() {
		ObjectsView objectsView = ObjectsView.getInstance();
		expandedSections = objectsView.getObjectExpandedSections();
		objectsView.hide();
	}



}
