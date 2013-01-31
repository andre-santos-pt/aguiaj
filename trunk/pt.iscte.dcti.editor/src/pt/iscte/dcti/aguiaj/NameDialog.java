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
package pt.iscte.dcti.aguiaj;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NameDialog extends Dialog {		
	private String name;
	private final Set<String> existing;
	private final Shell shell;
	private final boolean dots;
	private final boolean spaces;
	
	public NameDialog(Shell parent, String path, Set<String> existingNames, boolean allowDots, boolean allowSpaces) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.existing = existingNames;
		this.dots = allowDots;
		this.spaces = allowSpaces;
		
		shell = new Shell(parent, getStyle());
		shell.setLayout(new FillLayout());
		shell.setSize(250, 30);
		setText("Name?");
		
		final Text text = new Text(shell, SWT.BORDER);
		text.setText(path);
		text.selectAll();
		text.addListener(SWT.Verify, new Listener() {				
			@Override
			public void handleEvent(Event event) {
				if(!Character.isLetterOrDigit(event.character) && 
				   !directionChar(event.character) &&
				   !dots && event.character == '.' &&
				   !spaces && event.character == ' ')
					event.doit = false;
			}
		});

		text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == SWT.CR) {
					if(existing.contains(text.getText())) {
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR);
						messageBox.setText("Error");
						messageBox.setMessage("Name already exists.");
						messageBox.open();
					}
					else {
						name = text.getText();
						shell.dispose();
					}
				}				
			}
			@Override
			public void keyReleased(KeyEvent e) {

			}
		});
		FillLayout layout = new FillLayout();
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		shell.setLayout(layout);
		shell.setText(getText());
		shell.setLocation(getParent().getBounds().x + 170, getParent().getBounds().y + 220);
	}

	private boolean directionChar(char character) {
		return 
		character == SWT.BS ||
		character == SWT.DEL ||
		character == SWT.HOME ||
		character == SWT.END ||
		character == SWT.ARROW_LEFT ||
		character == SWT.ARROW_RIGHT;
	}

	public void open() {
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	public String getName() {
		return name;
	}

}
