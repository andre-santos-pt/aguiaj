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
package pt.org.aguiaj.aspects;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;

import pt.org.aguiaj.classes.ClassesView;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.Command;
import pt.org.aguiaj.core.commands.HistoryView;
import pt.org.aguiaj.core.commands.JavaBarView;
import pt.org.aguiaj.core.commands.ReloadClassesCommand;
import pt.org.aguiaj.core.commands.RemoveObjectCommand;
import pt.org.aguiaj.core.commands.RemoveReferenceCommand;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.JavaCommandWithReturn;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.commands.java.SeparateThreadCommand;
import pt.org.aguiaj.objects.ObjectsView;



public aspect CommandMonitor {
	
	private LinkedList<JavaCommand> activeCommands;
	
	public CommandMonitor() {
		activeCommands = new LinkedList<JavaCommand>();
	}

	public static CommandMonitor getInstance() {
		return CommandMonitor.aspectOf();
	}
	
	// adds every Java command to the stack
	after(JavaCommand command) : 
		execution(void JavaCommand.execute()) && target(command) {
		
		if(command instanceof JavaCommandWithReturn &&
				((JavaCommandWithReturn) command).isSilent())
			return;
		
		if(command instanceof SeparateThreadCommand) {
			((SeparateThreadCommand) command).waitToFinish();
		}
		
		if(!command.failed()) {
			addToStack(command);			
		}
			
	}
	
	// updates objects and classes (except if reloading)
	after(JavaCommand command) : 
		call(void JavaCommand.execute()) && target(command) && !this(ReloadClassesCommand) {
		
		if(command instanceof JavaCommandWithReturn &&
				((JavaCommandWithReturn) command).isSilent())
			return;
		
		if(command instanceof SeparateThreadCommand) {
			((SeparateThreadCommand) command).waitToFinish();
		}
		
		ObjectsView.getInstance().updateObjectWidgets();
		ClassesView.getInstance().updateClassWidgets();	
	}

	// updates objects and classes (except if reloading)
	after(JavaCommand command) : 
		execution(void JavaCommand.execute()) && this(command) && !this(JavaCommandWithReturn) {
		JavaBarView.getInstance().highlight(command.getJavaInstruction());
	}
	
	// show primitive values returned from method invocation commands
	after(MethodInvocationCommand command) : 
		call(void JavaCommand.execute()) && target(command) && !this(ReloadClassesCommand) {
		
		if(command.isSilent())
			return;
		
		command.waitToFinish();
		
		Object result = command.getResultingObject();

		if(result instanceof Double) {
			try {
				result = new BigDecimal((Double) result).doubleValue();
			}
			catch(NumberFormatException e) {
				result = 0.0;
			}
		}
		Class<?> returnType = command.getMethod().getReturnType();
		if(result != null && returnType.isPrimitive() && !returnType.equals(void.class)) {	
			JavaBarView.getInstance().highlight(command.getJavaInstruction());
			SWTUtils.showMessage(UIText.RETURN_VALUE.get(), result + "", SWT.ICON_WORKING);
			JavaBarView.getInstance().clear();
		}	
	}
	
	// after executing a command that results in an object
	// if primitive, skip
	// if object is in the area, highlight
	// add the object to the area otherwise
	after(JavaCommandWithReturn command) : 
		call(void JavaCommand.execute()) && target(command) {
		
		if(command.isSilent())
			return;
		
		if(command instanceof SeparateThreadCommand) {
			((SeparateThreadCommand) command).waitToFinish();
		}
		
		if(command.failed())
			return;
		
		Object invoker = thisJoinPoint.getThis();
		Object result = command.getResultingObject();
		Class<?> refType = command.getReferenceType();
				
		if(refType.isPrimitive() && !refType.equals(void.class))
			return;
		
		ObjectsView view = ObjectsView.getInstance();
		
		if(result != null) {
			view.addObjectWidget(result, command.getReference(), refType);
			
			if(!(invoker instanceof ReloadClassesCommand)) {
				JavaBarView.getInstance().highlight(command.getJavaInstruction());
			}
		}
		else {
			if(!refType.isPrimitive())
				view.addReference(refType, command.getReference(), null);
			
			if(!(invoker instanceof ReloadClassesCommand)) {
				JavaBarView.getInstance().highlight(command.getJavaInstruction());
			}
		}
	}

	
	// after removing an object, search for commands that have that object as
	// resulting object and remove them from the list
	after(RemoveObjectCommand command) : 
		execution(void Command.execute()) && target(command) {
		
		int index = -1;
		
		for(int i = 0; i < activeCommands.size(); i++) {
			JavaCommand cmd = activeCommands.get(i);

			if(cmd instanceof JavaCommandWithReturn) {
				JavaCommandWithReturn jcmd = (JavaCommandWithReturn) cmd;
				if(jcmd.getResultingObject() == command.getObject())
					index = i;
			}			
		}			
		
		if(index != -1)
			activeCommands.remove(index);
	}
	
	// after removing a reference, search for commands that have that reference 
	// and remove them from the list
	after(RemoveReferenceCommand command) : 
		execution(void Command.execute()) && target(command) {
		
		int index = -1;
		
		for(int i = 0; i < activeCommands.size(); i++) {
			JavaCommand cmd = activeCommands.get(i);
			if(cmd instanceof JavaCommandWithReturn) {
				JavaCommandWithReturn jcmd = (JavaCommandWithReturn) cmd;
				if(jcmd.getReference().equals(command.referenceName()))
					index = i;
			}
		}
		
		if(index != -1)
			activeCommands.remove(index);
	}
	
	private void addToStack(JavaCommand command) {
		activeCommands.add(command);
		HistoryView.getInstance().add(command);
	}
	
	public void clearStack() {
		activeCommands.clear();
	}

	public List<JavaCommand> getActiveCommands() {
		return activeCommands;
	}

	public boolean isFirstCommand(JavaCommand command) {
		return activeCommands.getFirst() == command;
	}

	public boolean isLastCommand(JavaCommand command) {
		return activeCommands.getLast() == command;
	}

	public JavaCommand getLastCommand() {
		return activeCommands.isEmpty() ? null : activeCommands.getLast();
	}

	public JavaCommand getCommandBefore(JavaCommand command) {
		if(command == null && !activeCommands.isEmpty())
			return activeCommands.getLast();

		int index = activeCommands.indexOf(command);
		if(index - 1 >= 0)
			return activeCommands.get(index - 1);

		return null;
	}

	public JavaCommand getCommandAfter(JavaCommand command) {
		int index = activeCommands.indexOf(command);
		if(index + 1 < activeCommands.size())
			return activeCommands.get(index + 1);
		
		return null;
	}
}
