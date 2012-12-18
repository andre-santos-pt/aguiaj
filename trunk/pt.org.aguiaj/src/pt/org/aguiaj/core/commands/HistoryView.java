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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.core.AguiaJParam;
import pt.org.aguiaj.core.commands.java.JavaCommand;
import pt.org.aguiaj.extensibility.AguiaJContribution;
import pt.org.aguiaj.aspects.CommandMonitor;



public class HistoryView extends ViewPart {
	public static class Open extends AbstractHandler {
		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			SWTUtils.showView(AguiaJContribution.HISTORY_VIEW);
			return null;
		}	
	}
	
	private List list;
	private static HistoryView instance;

	public HistoryView() {
		instance = this;
	}

	public static HistoryView getInstance() {
		if(instance == null)
			SWTUtils.showView(AguiaJContribution.HISTORY_VIEW);
		
		return instance;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new FillLayout());

		list = new List(composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		FontData data = new FontData(AguiaJParam.FONT.getString(), AguiaJParam.HUGE_FONT.getInt(), SWT.NONE);
		list.setFont(new Font(Display.getDefault(), data));		
		Menu menu = buildMenu(parent);		
		list.setMenu(menu);
		CommandMonitor.getInstance().addCommandEventListener(new CommandMonitor.CommandEventListener() {
			public void commandExecuted(JavaCommand cmd) {
				add(cmd);
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

		MenuItem clearItem = new MenuItem(menu, SWT.PUSH);
		clearItem.setText("Clear");
		clearItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				clear();
			}
		});

		return menu;
	}

	private void add(JavaCommand command) {
		list.add(command.toString() + ";");	
		list.setSelection(list.getItemCount()-1);
	}

	public void clear() {
		list.removeAll();
	}

	@Override
	public void dispose() {
		super.dispose();
		instance = null;
	}
	
	@Override
	public void setFocus() {

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
