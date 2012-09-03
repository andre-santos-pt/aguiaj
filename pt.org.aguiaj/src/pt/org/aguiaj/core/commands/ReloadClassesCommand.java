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
package pt.org.aguiaj.core.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;

import pt.org.aguiaj.aspects.CommandMonitor;
import pt.org.aguiaj.aspects.ObjectModel;
import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.classes.ClassesView;
import pt.org.aguiaj.core.Inspector;
import pt.org.aguiaj.core.commands.java.ConstructorInvocationCommand;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.exceptions.ExceptionHandler;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.Parser;
import pt.org.aguiaj.objects.ObjectWidget;
import pt.org.aguiaj.objects.ObjectsView;

public class ReloadClassesCommand extends AbstractHandler {
	private IPath workingDir = null;

	public void setWorkingDir(IPath workingDir) {
		this.workingDir = workingDir;
	}


	@Override
	//	@MonitorExecution
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<String> commands = new ArrayList<String>();
		for(JavaCommand c : CommandMonitor.aspectOf().getActiveCommands()) {
			String instruction = c.getJavaInstruction();
			if(instruction != null)
				commands.add(instruction);
		}

		ObjectsView objectsView = ObjectsView.getInstance();

		List<String> expandedPrivates = new ArrayList<String>();
		List<String> expandedOperations = new ArrayList<String>();
		
		if(objectsView != null) {
			expandedPrivates.addAll(objectsView.getReferencesForExpandedPrivatesObjects());
			expandedOperations.addAll(objectsView.getReferencesForExpandedOperationsObjects());
			objectsView.hide();
			objectsView.removeAll();
		}
		
		
		CommandMonitor.getInstance().clearStack();
		ClassModel.getInstance().clearClasses();
		ExceptionHandler.INSTANCE.clearErrors();
		HistoryView.getInstance().clear();	

		Inspector.loadInspectionPolicy();
		ClassesView.getInstance().reload(workingDir);				

		List<Class<?>> blackList = new ArrayList<Class<?>>();
		for(String command : commands) {
			try {
				Instruction instruction = Parser.accept(command, 
						ObjectModel.aspectOf().getReferenceTable(), 
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

					javaCommand.execute();

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

		objectsView.updateObjectWidgets();

		for(String ref : expandedPrivates) {
			ObjectWidget widget = objectsView.getObjectWidgetByReference(ref);
			if(widget != null)
				widget.showPrivateAttributes(true);
		}
		
		for(String ref : expandedOperations) {
			ObjectWidget widget = objectsView.getObjectWidgetByReference(ref);
			if(widget != null)
				widget.showOperations(true);
		}

		ClassesView.getInstance().updateClassWidgets();	
		JavaBarView.getInstance().clear();

		objectsView.show();

		return null;
	}



}
