package pt.org.aguiaj.eclipse;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;

import pt.org.aguiaj.extensibility.ExceptionTrace;
import pt.org.aguiaj.extensibility.TraceLocation;

public class ExceptionTraceView extends ViewPart {
	public static final String ID = "pt.org.aguiaj.eclipse.exceptionview";

	private static ExceptionTraceView instance;

	private Tree tree;

	public static ExceptionTraceView getInstance() {
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
					gotoLine((TraceLocation) selection[0].getData());
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
			gotoLine((TraceLocation) item.getData());
		}
		
	}
	
	private String methodCall(TraceLocation loc) {
		return loc.className + "." + loc.methodName + "(..)";
	}
	
	
	private void gotoLine(TraceLocation loc) {
		IJavaElement element = null;
		try {
			element = Activator.getProject().findElement(new Path(loc.fileName));
		} catch (JavaModelException e1) {
			e1.printStackTrace();
		}
		
		if(element == null)
			return;
		
		IFile file = (IFile) element.getResource();
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(); 

		String pers = Activator.getPerspective();
		if(pers == null)
			pers = JavaUI.ID_PERSPECTIVE;

		try {			
			PlatformUI.getWorkbench().showPerspective(pers, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		
		try {
			IMarker marker = file.createMarker(IMarker.TEXT);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(IMarker.LINE_NUMBER, new Integer(loc.line));
			marker.setAttributes(map);
			IDE.openEditor(page, marker); 
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		tree.setFocus();
	}

	public void clear() {
		tree.removeAll();
	}

}
