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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.common.Fonts;
import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.extensibility.AguiaJContribution;
import pt.org.aguiaj.extensibility.JavaCommand;
import pt.org.aguiaj.extensibility.ObjectEventListener;
import pt.org.aguiaj.extensibility.ObjectEventListenerAdapter;
import pt.org.aguiaj.objects.ObjectModel;



public class HistoryView extends ViewPart {
	public static class Open extends AbstractHandler {
		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			SWTUtils.showView(AguiaJContribution.HISTORY_VIEW);
			return null;
		}	
	}
	
	private List list;

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new FillLayout());

		list = new List(composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		
		Fonts.set(list, AguiaJParam.BIG_FONT);
		
		Menu menu = buildMenu(parent);		
		list.setMenu(menu);
		
		for(JavaCommand cmd : ObjectModel.getInstance().getActiveCommands()) {
			add(cmd);
		}
		
		final ObjectEventListener listener = new ObjectEventListenerAdapter() {
			public void commandExecuted(JavaCommand cmd) {
				if(!cmd.failed())
					add(cmd);
			}
			
			@Override
			public void clearAll() {
				clear();
			}

			@Override
			public void commandRemoved(JavaCommand cmd) {
				for(String item : list.getItems())
					if(item.equals(statement(cmd)))
						list.remove(item);
			}
		};
		ObjectModel.getInstance().addEventListener(listener);
		
		list.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				ObjectModel.getInstance().removeEventListener(listener);
			}
		});
	}

	private Menu buildMenu(Composite parent) {
		Menu menu = new Menu(parent.getShell(), SWT.POP_UP);

		MenuItem copyItem = new MenuItem(menu, SWT.PUSH);
		copyItem.setText("Copy");
		copyItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				copySelectionToClipboard();
			}
		});

		MenuItem selectAllItem = new MenuItem(menu, SWT.PUSH);
		selectAllItem.setText("Select all");
		selectAllItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				list.selectAll();
			}
		});

//		MenuItem clearItem = new MenuItem(menu, SWT.PUSH);
//		clearItem.setText("Clear");
//		clearItem.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				clear();
//			}
//		});
		
		return menu;
	}

	private String statement(JavaCommand command) {
		return command.getJavaInstruction() + ";";
	}
	
	private void add(JavaCommand command) {
		list.add(statement(command));	
		list.setSelection(list.getItemCount()-1);
	}

	private void clear() {
		list.removeAll();
	}

	@Override
	public void setFocus() {
		list.setFocus();
	}

	private static final String newline = System.getProperty("line.separator");

	public void copySelectionToClipboard() {
		Clipboard clipboard = new Clipboard(Display.getDefault());
		TextTransfer textTransfer = TextTransfer.getInstance();
		StringBuffer content = new StringBuffer();
		for(String item : list.getSelection()) {
			content.append(item);
			content.append(newline);
		}
		clipboard.setContents(new Object[]{content.toString()}, new Transfer[]{textTransfer});
	}
}
