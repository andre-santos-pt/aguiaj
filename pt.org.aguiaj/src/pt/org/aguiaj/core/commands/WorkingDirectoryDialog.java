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
package pt.org.aguiaj.core.commands;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.common.SWTUtils;

public class WorkingDirectoryDialog extends Dialog {		
		private IPath path;
		
		public WorkingDirectoryDialog(Shell parent, IPath path) {
			super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			this.path = path;
			setText("Select working directory");
		}

		public IPath open() {
			Shell shell = new Shell(getParent(), getStyle());
			shell.setLayout(new FillLayout());
			shell.setText(getText());
			shell.setLocation(getParent().getBounds().x + 170, getParent().getBounds().y + 220);
			createContents(shell);
			shell.pack();
			shell.open();
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			return path;
		}

		private void createContents(final Shell shell) {
			Composite comp = new Composite(shell, SWT.NONE);
			comp.setLayout(new RowLayout(SWT.VERTICAL));
			
			Composite inputComp = new Composite(comp, SWT.NONE);
			inputComp.setLayout(new RowLayout(SWT.HORIZONTAL));
			new Label(inputComp, SWT.NONE).setText("Working directory");
			final Text pathText = new Text(inputComp, SWT.BORDER);
			if(path != null)
				pathText.setText(path.toFile().getAbsolutePath());
			pathText.setLayoutData(new RowData(300, 15));
			
			
			Button browse = new Button(inputComp, SWT.PUSH);
			browse.setText("Browse...");
			browse.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dlg = new DirectoryDialog(shell);
					dlg.setText("Select working directory");
					dlg.setMessage("Select the folder that contains the compiled classes that you are working on (e.g. the bin folder on an Eclipse project)");
					String ret = dlg.open();
					if(ret != null)
						pathText.setText(ret);
				}
			});

			Composite okComp = new Composite(comp, SWT.NONE);
			okComp.setLayout(new RowLayout(SWT.HORIZONTAL));
			Button ok = new Button(okComp, SWT.PUSH);
			ok.setText("OK");
			ok.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					path = new Path(pathText.getText());
					if(!path.toFile().exists() || !path.toFile().isDirectory()) {
						String message = path.toOSString() + " is not a valid path of a directory.";
						SWTUtils.showMessage("Please insert a valid directory", message, SWT.ICON_WARNING);
						path = null;
					}
					else
						shell.dispose();
				}
			});

			Button cancel = new Button(okComp, SWT.PUSH);
			cancel.setText("Cancel");
			cancel.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent e) {
					shell.dispose();
				}
			});
			ok.setSelection(true);
		}

}
