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
package pt.org.aguiaj.eclipse;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;

import pt.org.aguiaj.core.commands.ChangeWorkingDirCommand;


public class SwitchProject implements IViewActionDelegate {
	private IWorkbench workbench;

	@Override
	public void run(IAction action) {
		new SwitchProjectDialog(workbench.getActiveWorkbenchWindow().getShell()).open();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		workbench = view.getSite().getWorkbenchWindow().getWorkbench();
	}

	static class SwitchProjectDialog extends Dialog {		
		public SwitchProjectDialog(Shell parent) {
			super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			setText("Switch Project");
		}

		public void open() {
			Shell shell = new Shell(getParent(), getStyle());
			FillLayout layout = new FillLayout();
			layout.marginHeight = 10;
			layout.marginWidth = 10;
			shell.setLayout(layout);
			shell.setText(getText());
			shell.setLocation(getParent().getBounds().x + 170, getParent().getBounds().y + 220);
			final Combo combo = new Combo(shell, SWT.DROP_DOWN);
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();			
			for(IProject proj : projects) {
				try {
					if(proj.isOpen() && proj.hasNature(JavaCore.NATURE_ID)) {
						combo.add(proj.getName());
						combo.setData(proj.getName(), proj.getNature(JavaCore.NATURE_ID));
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}			
			
			IJavaProject currentProj = Activator.getProject();
			if(currentProj != null) {
				String[] items = combo.getItems();
				for(int i = 0; i < items.length; i++) {
					if(items[i].equals(currentProj.getProject().getName())) {
						combo.select(i);
						break;
					}
				}
			}

			combo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int i = combo.getSelectionIndex();
					IJavaProject proj = (IJavaProject) combo.getData(combo.getItem(i));
					try {
						IPath path = proj.getProject().getLocation().append(proj.getOutputLocation().removeFirstSegments(1));
						ChangeWorkingDirCommand command = new ChangeWorkingDirCommand();
						command.setDirectory(path);
						command.execute(null);
					} 
					catch (Exception ex) {
						ex.printStackTrace();
					} 
					Activator.setProject(proj);
				}
			});

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
}
