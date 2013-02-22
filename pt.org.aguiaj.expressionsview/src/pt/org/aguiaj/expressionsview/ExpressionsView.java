package pt.org.aguiaj.expressionsview;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.javainterpreter.JavaInterpreter;

import pt.org.aguiaj.expressionsview.builder.SampleNature;

public class ExpressionsView extends ViewPart implements IPartListener2 {

	private IEditorPart editorPart;
	private TableViewer viewer;
	private JavaInterpreter interpreter;
	
	private static ExpressionsView _instance;

	private static final Color BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	private Map<IEditorInput, List<Expression>> expressions;

	public ExpressionsView() {
		_instance = this;
		expressions = new HashMap<IEditorInput, List<Expression>>();
		interpreter = new JavaInterpreter();
	}

	public static ExpressionsView getInstance() {
		return _instance;
	}

	void refresh() {
		Class<?> clazz = loadClass();
		for(Expression exp : expressions.get(editorPart.getEditorInput()))
			exp.setClass(clazz);
		
		interpreter.clear();
		
		if(viewer != null)
			viewer.setInput(editorPart.getEditorInput());
	}

	@Override
	public void setFocus() {
		viewer.getTable().setFocus();
	}

	@Override
	public void createPartControl(Composite parent) {
		getSite().getWorkbenchWindow().getPartService().addPartListener(this);

		createTable(parent);		
		createExpressionColumn();
		createResultColumn();
		setContentProvider();
	}



	private Class<?> loadClass() {
		if(editorPart != null) {

			IFileEditorInput input = (IFileEditorInput) editorPart.getEditorInput();
			IResource r = (IResource) input.getAdapter(IResource.class);
			IProject proj = r.getProject();
			try {
				if(proj.hasNature(JavaCore.NATURE_ID)) {
					ProjectClassLoader loader = new ProjectClassLoader(getClass().getClassLoader(), proj);
					String name = input.getFile().getLocation().removeFileExtension().lastSegment();
					return loader.loadClass(name);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	


	private void createTable(Composite parent) {
		viewer = new TableViewer(parent);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setFont(new Font(Display.getDefault(), new FontData("Courier", 16, SWT.NONE)));

		viewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = 30;
			}
		});
		
	}

	private void setContentProvider() {
		viewer.setContentProvider(new IStructuredContentProvider() {
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			@Override
			public void dispose() {
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return expressions.get(inputElement).toArray();
			}
		});
	}

	private void createExpressionColumn() {
		final TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("Function call");
		col.getColumn().setWidth(200);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Expression) element).getValue();
			}
			
		});
		
		col.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				Expression exp = (Expression) element;
				exp.setValue(value.toString());
				getViewer().update(element, null);
				refresh();
			}

			@Override
			protected Object getValue(Object element) {
				return ((Expression) element).getValue();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(viewer.getTable());
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		viewer.getTable().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.keyCode == SWT.CR) {
					int index = viewer.getTable().getSelectionIndex();
					if(index != -1)
						viewer.editElement(viewer.getElementAt(index), 0);
				}
				else if(e.keyCode == SWT.DEL) {
					int index = viewer.getTable().getSelectionIndex();
					if(index != -1) {
						expressions.get(editorPart.getEditorInput()).remove(index);
						refresh();
					}
				}
				else if(e.keyCode == SWT.ARROW_DOWN) {
					int index = viewer.getTable().getSelectionIndex();
					if(index == viewer.getTable().getItemCount() - 1) {
						Expression exp = new Expression(interpreter, loadClass(), viewer.getTable().getItem(index).getText());
						expressions.get(editorPart.getEditorInput()).add(exp);
						refresh();
						viewer.editElement(viewer.getElementAt(viewer.getTable().getItemCount()-1), 0);
					}
				}
			}
		});

		viewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				expressions.get(editorPart.getEditorInput()).add(new Expression(interpreter, loadClass(), ""));
				refresh();
				viewer.editElement(viewer.getElementAt(viewer.getTable().getItemCount()-1), 0);
			}
		});

		//		Menu menu = new Menu(table.getControl().getShell(), SWT.POP_UP);
		//		MenuItem item = new MenuItem(menu, SWT.PUSH);
		//		item.setText("Add item");
		//		item.addSelectionListener(new SelectionAdapter() {
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//				expressions.get(editorPart.getEditorInput()).add("?");
		//				refresh();
		//			}
		//		});
		//		table.getTable().setMenu(menu);
	}

	private void createResultColumn() {
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("Result");
		col.getColumn().setWidth(400);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				try {
					return ((Expression) element).evaluate();
				}
				catch(RuntimeException ex) {
					return ex.getMessage();
				}
			}

			@Override
			public Color getForeground(Object element) {
				return super.getForeground(element);
			}
		});


	}

	private void createErrorColumn() {
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("Error");
		col.getColumn().setWidth(300);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Expression) element).getErrorMessage();
			}
		});
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		IWorkbenchPart part = partRef.getPart(true);
		if(part instanceof IEditorPart) {
			// TODO: check if java editor
			editorPart = (IEditorPart) part;
			IEditorInput input = editorPart.getEditorInput();
			if(input instanceof FileEditorInput) {
				IProject proj = ((FileEditorInput) input).getFile().getProject();
				toggleNature(proj);
			}
			if(!expressions.containsKey(input))
				expressions.put(input, new ArrayList<Expression>());

			refresh();
		}

	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	private void toggleNature(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (SampleNature.NATURE_ID.equals(natures[i])) 
					return;
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = SampleNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}

}
