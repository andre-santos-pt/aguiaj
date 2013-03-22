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

import java.math.BigDecimal;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.classes.ClassModel;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.UIText;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.core.commands.java.MethodInvocationCommand;
import pt.org.aguiaj.core.interpreter.Instruction;
import pt.org.aguiaj.core.interpreter.ParseException;
import pt.org.aguiaj.core.interpreter.Parser;
import pt.org.aguiaj.extensibility.AguiaJContribution;
import pt.org.aguiaj.objects.ObjectModel;


public class JavaBarView extends ViewPart implements ISizeProvider {
	private Composite bar;	

	private static JavaBarView instance;
	private Text instructionBar;
	private JavaCommand lastCommand;
	private ObjectModel model;

	public void createPartControl(Composite parent) {
		instance = this;
		model = ObjectModel.getInstance();
		bar = new Composite(parent, SWT.BORDER);
		bar.setLayout(new FillLayout());		
		createInstructionBar();
		model.addEventListener(new ObjectModel.EventListenerAdapter() {
			@Override
			public void commandExecuted(JavaCommand cmd) {
				setLine(cmd.getJavaInstruction());
				if(cmd instanceof MethodInvocationCommand) {
					Class<?> returnType = ((MethodInvocationCommand) cmd).getMethod().getReturnType();
					Object result = ((MethodInvocationCommand) cmd).getResultingObject();
					if(result instanceof Double) {
						try {
							result = new BigDecimal((Double) result).doubleValue();
						}
						catch(NumberFormatException e) {
							result = 0.0;
						}
					}
					if(result != null && returnType.isPrimitive() && !returnType.equals(void.class)) {	
						SWTUtils.showMessage(UIText.RETURN_VALUE.get(), result + "", SWT.ICON_WORKING);
						clear();
					}	
				}
			}
		});
	}


	public void setFocus() {
		bar.setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		instance = null;
	}

	public static JavaBarView getInstance() {
		if(instance == null)
			SWTUtils.showView(AguiaJContribution.JAVABAR_VIEW);

		return instance;
	}

	private void createInstructionBar() {
		instructionBar = new Text(bar, SWT.CENTER);
		updateFont();
		instructionBar.setToolTipText("Java Bar (type in Java statements to create and interact with objects)");
		instructionBar.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent event) {
				if(clearJob != null)
					clearJob.cancel();
				
				if(event.keyCode == SWT.CR && !instructionBar.getText().equals("")) {
					executeCommand();
//					clear();
				}
				else if(event.keyCode == SWT.ARROW_UP) {
					if(lastCommand != null && model.isFirstCommand(lastCommand))
						clear();
					else {
						lastCommand = model.getCommandBefore(lastCommand);
						if(lastCommand != null)
							setLine(lastCommand.getJavaInstruction());
					}

				}
				else if(event.keyCode == SWT.ARROW_DOWN) {
					if(lastCommand != null && model.isLastCommand(lastCommand))
						clear();
					else {
						lastCommand = model.getCommandAfter(lastCommand);
						if(lastCommand != null)
							setLine(lastCommand.getJavaInstruction());
					}
				}
			}
		});
	}

	public void updateFont() {
		FontData data = new FontData(AguiaJParam.FONT.getString(), AguiaJParam.JAVABAR_FONT.getInt(), SWT.NONE);
		instructionBar.setFont(new Font(Display.getDefault(), data));
	}

	public int computePreferredSize(boolean width, int availableParallel,
			int availablePerpendicular, int preferredResult) {

		int size = width ?  preferredResult : 20;
		return size;
	}


	public int getSizeFlags(boolean width) {	
		return 0;
	}

	private void executeCommand() {
		String input = instructionBar.getText().trim();
		if(input.endsWith(";"))
			input = input.substring(0,input.length()-1);

		Instruction instruction = null;
		JavaCommand command = null;

		try {
			instruction = Parser.accept(input, 
					ObjectModel.getInstance().getReferenceTable(), 
					ClassModel.getInstance().getAllClasses());

			if(instruction != null) {
				try {
					command = instruction.getCommand();
				}
				catch(RuntimeException ex) {
					SWTUtils.showMessage("Java Bar", UIText.RUNTIME_ERROR.get(ex.getMessage()), SWT.ERROR);
				}
			}
			else
				SWTUtils.showMessage("Java Bar", UIText.SYNTAX_ERROR.get(), SWT.ERROR);

			//			command = Parser.parse(input);
		}	
		catch(ParseException e) {
			SWTUtils.showMessage(e.cause, e.detail, SWT.ERROR);
		}
		catch(Exception e) {			
			e.printStackTrace();
		}			

		if(command != null) {
			ObjectModel.getInstance().execute(command);
			try {
				command.execute();
			}
			catch(ParseException e) {
				SWTUtils.showMessage(e.cause, e.detail, SWT.ERROR);
			}
			catch(RuntimeException ex) {
				SWTUtils.showMessage("Java Bar", ex.getMessage(), SWT.ERROR);
			}
			lastCommand = null;	
			if(!command.failed())
				clear();
		}
	}

	private Job clearJob;
	public void setLine(final String line) {
		instructionBar.setText(line);
		if(clearJob != null)
			clearJob.cancel();
		clearJob = new Job("javabar") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						instructionBar.setText("");
					}
				});
				return Status.OK_STATUS;
			}
		};
		clearJob.schedule(AguiaJParam.HIGHLIGHT_TIMEOUT.getInt() * 1000);
	}

	private void clear() {
		instructionBar.setText("");
		lastCommand = null;
	}
}
