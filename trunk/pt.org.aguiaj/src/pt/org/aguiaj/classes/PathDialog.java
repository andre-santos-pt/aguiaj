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
package pt.org.aguiaj.classes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PathDialog extends Dialog {		
		private String path;
		
		public PathDialog(Shell parent, String path) {
			super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			this.path = path;
			setText("Jar location");
		}

		public void open() {
			Shell shell = new Shell(getParent(), getStyle());
			FillLayout layout = new FillLayout();
			layout.marginHeight = 10;
			layout.marginWidth = 10;
			shell.setLayout(layout);
			shell.setText(getText());
			shell.setLocation(getParent().getBounds().x + 170, getParent().getBounds().y + 220);
			Text text = new Text(shell,SWT.BORDER);
			text.setText(path);
			text.selectAll();
			shell.pack();
			shell.open();
			Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			
		}
		
}
