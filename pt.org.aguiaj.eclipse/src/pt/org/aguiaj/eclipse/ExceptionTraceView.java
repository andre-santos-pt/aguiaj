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
package pt.org.aguiaj.eclipse;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.extensibility.ExceptionTrace;
import pt.org.aguiaj.extensibility.TraceLocation;

public class ExceptionTraceView extends ViewPart {
	public static final String ID = "pt.org.aguiaj.eclipse.exceptionview";

	private static ExceptionTraceView instance;

	private Tree tree;

	public static ExceptionTraceView getInstance() {
		if(instance == null) {
			try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ExceptionTraceView.ID);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	@Override
	public void createPartControl(Composite parent) {
		instance = this;
		parent.setLayout(new FillLayout());
		tree = new Tree(parent, SWT.NONE);
		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] selection = tree.getSelection();
				if(selection.length == 1 && selection[0].getData() != null) {
//					gotoLine((TraceLocation) selection[0].getData());
					TraceLocation loc = (TraceLocation) selection[0].getData();
					EclipseUtil.gotoLine(loc.fileName, loc.line);
				}
			}
		});
	}

	public void setInput(ExceptionTrace trace) {
		tree.removeAll();
		List<TraceLocation> traceLocs = trace.getTrace();

		if(!traceLocs.isEmpty()) {
			TraceLocation loc = traceLocs.get(0);
			
			TreeItem item  = new TreeItem(tree, SWT.NONE);
			item.setText(" " + methodCall(loc));
			if(traceLocs.size() == 1) {
				item  = new TreeItem(item, SWT.NONE);
				item.setText(" " + loc.line + ": " + trace.getMessage());
				item.setData(loc);
			}
			
			for(int i = 1; i < traceLocs.size(); i++) {
				item  = new TreeItem(item, SWT.NONE);
				item.setText(" " + traceLocs.get(i-1).line + ": " + methodCall(traceLocs.get(i)));
				item.setData(traceLocs.get(i-1));

				if(i == traceLocs.size() - 1) {
					item  = new TreeItem(item, SWT.NONE);
					item.setText(" " + traceLocs.get(i).line + ": " + trace.getMessage());
					item.setData(traceLocs.get(i));
				}
			}
			
			tree.showItem(item);
			tree.select(item);
//			gotoLine((TraceLocation) item.getData());
		}
		
	}
	
	private String methodCall(TraceLocation loc) {
		return loc.className + "." + loc.methodName + "(..)";
	}
	
	
	@Override
	public void setFocus() {
		tree.setFocus();
	}

	public void clear() {
		tree.removeAll();
	}

}
