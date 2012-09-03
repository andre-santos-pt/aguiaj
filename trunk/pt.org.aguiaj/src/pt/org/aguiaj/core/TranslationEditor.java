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
package pt.org.aguiaj.core;


import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import pt.org.aguiaj.extensibility.AguiaJContribution;

public class TranslationEditor extends Dialog {
	public static class Open extends AbstractHandler {
		@Override
		public Object execute(ExecutionEvent event) throws ExecutionException {
			new TranslationEditor(Display.getDefault().getActiveShell()).open();
			return null;
		}
	}

	private final Shell shell;
	private final Text nameField;
	private TableViewer table;
	private final Map<UIText, String> map;
	
	public TranslationEditor(Shell parent) {
		super(parent);
		shell = new Shell(parent);
		map = new HashMap<UIText, String>();
		
		shell.setLayout(new RowLayout(SWT.VERTICAL));
		shell.setText(UIText.NEW_LANGUAGE_PLUGIN.get());
		
		
		new Label(shell, SWT.WRAP).setText(UIText.NEW_LANGUAGE_FEATURE.get());
		nameField = new Text(shell, SWT.BORDER);
		nameField.setText(UIText.LANGUAGE_NAME.get());
		
		createTable();
		table.setInput(UIText.values());	

		createGenerateButton();
		createCloseButton();
		
	}

	private void createCloseButton() {
		Button closeButton = new Button(shell, SWT.PUSH);
		closeButton.setText(UIText.CLOSE.get());
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
	}

	private void createGenerateButton() {
		Button genButton = new Button(shell, SWT.PUSH);
		genButton.setText(UIText.GENERATE_PLUGIN.get());
		genButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				writePlugin();
			}
		});
	}

	private void createTable() {
		table = new TableViewer(shell,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		TableViewerColumn originalCol = new TableViewerColumn(table, SWT.NONE);
		TableColumn col = originalCol.getColumn();
		col.setText(UIText.ORIGINAL_MESSAGE.get());
		col.setWidth(300);
		originalCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((UIText) element).get();
			}
			
			public Color getForeground(Object element) {
				if(!map.containsKey(element))
					return new Color(Display.getDefault(), 255, 0, 0);
				else
					return null;
			}
		});

		TableViewerColumn transCol = new TableViewerColumn(table, SWT.NONE);
		col = transCol.getColumn();
		col.setText(UIText.TRANSLATION.get());
		col.setWidth(300);
		transCol.setEditingSupport(new TranslationCellEditor());

		transCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return map.containsKey(element) ? map.get(element) : "";
			}
		});

		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		
		table.setContentProvider(new ArrayContentProvider());
	}

	private class TranslationCellEditor extends EditingSupport {
		private TextCellEditor editor;

		public TranslationCellEditor() {
			super(table);
			editor = new TextCellEditor(table.getTable());
		}

		protected void setValue(Object element, Object value) {
			map.put((UIText) element, value == null ? "" : value.toString());
			getViewer().update(element, null);
		}

		@Override
		protected Object getValue(Object element) {
			return map.containsKey((UIText) element) ? map.get(element) : "";
 		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}
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
	

	private static final String NEWLINE = System.getProperty("line.separator");
	private void writePlugin() {
		StringBuffer buffer = createPluginXML();
		
		IProgressMonitor progressMonitor = new NullProgressMonitor();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		
		IProject project = root.getProject(nameField.getText());
		try {
			project.create(progressMonitor);
			project.open(progressMonitor);
			IFile file = project.getFile("plugin.xml");
			ByteArrayInputStream stream = new ByteArrayInputStream(buffer.toString().getBytes("UTF-8"));
			file.create(stream, true, progressMonitor);
			
			IFolder metainf = project.getFolder("META-INF");
			metainf.create(true, true, progressMonitor);
			
			IFile manifest = metainf.getFile("MANIFEST.MF");
			stream = new ByteArrayInputStream(createManifestMF().toString().getBytes("UTF-8"));
			manifest.create(stream, true, progressMonitor);
		}
		catch(CoreException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private StringBuffer createPluginXML() {
		StringBuffer buffer = new StringBuffer();
		buffer
		.append("<plugin>" + NEWLINE)
		.append("<extension point=\""  + AguiaJContribution.EXTENSION_LANGUAGES + "\">" + NEWLINE)
		.append("<language id=\"" + nameField.getText() + "\">" + NEWLINE);
		
		for(Entry<UIText, String> entry : map.entrySet()) {
			buffer.append("<entry id=\"" + entry.getKey().name() + 
					"\" value=\"" + entry.getValue() + "\"/>" + NEWLINE);
		
		}
			
		buffer
		.append("</language>" + NEWLINE)
		.append("</extension>" + NEWLINE)
		.append("</plugin>" + NEWLINE);
		return buffer;
	}

	private StringBuffer createManifestMF() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Manifest-Version: 1.0");
		return buffer;
	}
}
