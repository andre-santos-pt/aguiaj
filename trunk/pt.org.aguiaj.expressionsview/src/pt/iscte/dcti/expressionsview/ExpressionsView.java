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
package pt.iscte.dcti.expressionsview;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eclipselabs.javainterpreter.Context;
import org.eclipselabs.javainterpreter.ExecutionException;
import org.eclipselabs.javainterpreter.JavaInterpreter;
import org.eclipselabs.javainterpreter.SimpleContext;

import pt.org.aguiaj.extensibility.AguiaJHelper;


public class ExpressionsView extends ViewPart implements IPartListener2 {

	private IEditorInput input;
	private TableViewer viewer;
	private JavaInterpreter interpreter;

	private static ExpressionsView _instance;

	//	private static final Color BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static final Color RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	private Map<IEditorInput, List<Expression>> expressions;

	private Map<String, Class<?>> aguiajClassMap;

	private VarContext context;

	private class VarContext implements Context {
		private Context context;
		private Set<Class<?>> set;

		public VarContext() {
			context = new SimpleContext(null, Math.class, String.class, Random.class);
			set = new HashSet<Class<?>>(1);
		}

		@Override
		public boolean isClassAvailable(String name) {
			for(Class<?> c : getImplicitClasses())
				if(c.getName().equals(name))
					return true;

			if(ProjectClassLoader.existsInLibrary(((FileEditorInput) input).getFile().getProject(), name))
				return true;

			return context.isClassAvailable(name);
		}

		@Override
		public Object getObject(String referenceName) {
			return context.getObject(referenceName);
		}

		@Override
		public Set<Class<?>> getImplicitClasses() {
			return Collections.unmodifiableSet(set);
		}

		@Override
		public Class<?> getClass(String name) {
			for(Class<?> c : getImplicitClasses())
				if(c.getName().equals(name))
					return c;

			return context.getClass(name);
		}

		@Override
		public boolean existsReference(String name) {
			return context.existsReference(name);
		}

		@Override
		public void addReference(Class<?> type, String name, Object object) {
			context.addReference(type, name, object);
		}

		@Override
		public Class<?> referenceType(String name) {
			return context.referenceType(name);
		}

		public void setImplicitClass(Class<?> c) {
			set.clear();
			set.add(c);
		}
	}

	public ExpressionsView() {
		_instance = this;

		expressions = new HashMap<IEditorInput, List<Expression>>();

		context = new VarContext();
		interpreter = new JavaInterpreter(context);

		IEditorPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(part != null)
			handlePartOpen(part);

		aguiajClassMap = new HashMap<String, Class<?>>();

		for(Class<?> c : AguiaJHelper.getAllPluginClasses())
			aguiajClassMap.put(c.getName(), c);

	}

	public static ExpressionsView getInstance() {
		return _instance;
	}

	public void addItem(String text) {
		if(input != null) {
			expressions.get(input).add(new Expression(interpreter, text));
			refresh();
			viewer.editElement(viewer.getElementAt(viewer.getTable().getItemCount()-1), 0);
		}
	}

	public boolean isItemSelected() {
		return viewer.getTable().getSelectionIndex() != -1;
	}

	public void removeSelectedItem() {
		int index = viewer.getTable().getSelectionIndex();
		if(index != -1) {
			expressions.get(input).remove(index);
			refresh();
			viewer.getTable().select(index);
		}
	}

	public void removeAllItems() {
		List<Expression> list = expressions.get(input);
		if(list != null) {
			list.clear();
			refresh();
		}
	}

	void refresh() {
		if(viewer != null && !viewer.getTable().isDisposed()) {
			viewer.setInput(input);
			context.setImplicitClass(loadClass());
		}
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
		if(input != null) {
			IResource r = (IResource) input.getAdapter(IResource.class);
			if(r != null) {
				IProject proj = r.getProject();
				try {
					if(proj.hasNature(JavaCore.NATURE_ID)) {
						IJavaProject javaProj = JavaCore.create(proj);
						ProjectClassLoader loader = new ProjectClassLoader(getClass().getClassLoader(), proj, aguiajClassMap);
						IPackageFragmentRoot[] packageFragmentRoot = javaProj.getAllPackageFragmentRoots();
						for (int i = 0; i < packageFragmentRoot.length; i++){
							if (packageFragmentRoot[i].getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT 
									&& !packageFragmentRoot[i].isArchive()) {
								IPath src = packageFragmentRoot[i].getCorrespondingResource().getProjectRelativePath();
								IPath path = ((FileEditorInput) input).getFile().getProjectRelativePath();
								int match = path.matchingFirstSegments(src);
								String name = path.removeFirstSegments(match).removeFileExtension().toString().replace('/', '.');
								// TODO several src folders
								return loader.loadClass(name);
							}
						}
					}
				}
				catch(ClassNotFoundException e) {

				} catch (CoreException e) {
					e.printStackTrace();
				}
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
				if(inputElement != null)
					return expressions.get(inputElement).toArray();
				else
					return new Object[0];
			}
		});
	}

	private void createExpressionColumn() {
		final TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("Function call");
		col.getColumn().setWidth(300);
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
				else if(e.keyCode == SWT.DEL || e.keyCode == SWT.BS) {
					removeSelectedItem();
				}
				else if(e.keyCode == SWT.ARROW_DOWN) {
					int index = viewer.getTable().getSelectionIndex();
					if(index == viewer.getTable().getItemCount() - 1 && viewer.getTable().getItemCount() != 0) {
						Expression exp = new Expression(interpreter, viewer.getTable().getItem(index).getText());
						expressions.get(input).add(exp);
						refresh();
						viewer.editElement(viewer.getElementAt(viewer.getTable().getItemCount()-1), 0);
					}
				}
			}
		});

		//		viewer.getTable().addMouseListener(new MouseAdapter() {
		//			@Override
		//			public void mouseDoubleClick(MouseEvent e) {
		//				if(input != null) {
		//					expressions.get(input).add(new Expression(interpreter, ""));
		//					refresh();
		//					viewer.editElement(viewer.getElementAt(viewer.getTable().getItemCount()-1), 0);
		//				}
		//			}
		//		});

		//		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

		//			@Override
		//			public void selectionChanged(SelectionChangedEvent event) {
		viewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = viewer.getTable().getSelectionIndex();
				Expression exp = (Expression) viewer.getElementAt(index);
				if(exp != null) {
					ExecutionException ex = exp.getException();
					if(ex != null) {
						int line = ex.getLine();
						try {
							IMarker marker = ((FileEditorInput) input).getFile().createMarker(IMarker.TEXT);
							HashMap<String, Object> map = new HashMap<String, Object>();
							map.put(IMarker.LINE_NUMBER, new Integer(line));
							marker.setAttributes(map);
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 
							IDE.openEditor(page, marker); 
							marker.delete();
						} catch (CoreException exc) {
							exc.printStackTrace();
						}
					}
				}
				else {
					if(input != null) {
						expressions.get(input).add(new Expression(interpreter, ""));
						refresh();
						viewer.editElement(viewer.getElementAt(viewer.getTable().getItemCount()-1), 0);
					}
				}
			}

		});
		//			}
		//		});

		//		Menu menu = new Menu(viewer.getControl().getShell(), SWT.POP_UP);
		//		MenuItem item = new MenuItem(menu, SWT.PUSH);
		//		item.setText("Add item");
		//		item.addSelectionListener(new SelectionAdapter() {
		//			@Override
		//			public void widgetSelected(SelectionEvent e) {
		//				expressions.get(editorPart.getEditorInput()).add("?");
		//				refresh();
		//			}
		//		});
		//		viewer.getTable().setMenu(menu);
	}

	private void createResultColumn() {
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		col.getColumn().setText("Result");
		col.getColumn().setWidth(600);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				EvaluateRunnable runnable = new EvaluateRunnable();
				runnable.expression = (Expression) element;
				Display.getDefault().syncExec(runnable);
				return runnable.result;
			}

			@Override
			public Color getForeground(Object element) {
				if(((Expression) element).valid())
					return super.getForeground(element);
				else
					return RED;
			}
		});
	}

	private static class EvaluateRunnable implements Runnable {
		Expression expression;
		String result;

		public void run() {
			try {
				result = expression.evaluate();
			}
			catch(RuntimeException ex) {
				result = ex.getMessage();
			}
		}
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
		handlePartOpen(partRef.getPart(true));
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		handlePartOpen(partRef.getPart(true));

	}

	private void handlePartOpen(IWorkbenchPart part) {
		if(part instanceof IEditorPart) {
			input = ((IEditorPart) part).getEditorInput();
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
		IWorkbenchPart part = partRef.getPart(true);
		if(part instanceof IEditorPart) {
			input = null;
			refresh();
		}
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		handlePartOpen(partRef.getPart(true));
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		handlePartOpen(partRef.getPart(true));
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {

	}

	private void toggleNature(IProject project) {
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (FunctionTestNature.NATURE_ID.equals(natures[i])) 
					return;
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = FunctionTestNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}

}
