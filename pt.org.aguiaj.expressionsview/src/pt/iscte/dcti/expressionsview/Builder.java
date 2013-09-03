package pt.iscte.dcti.expressionsview;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

public class Builder extends IncrementalProjectBuilder {

	public static final String ID = "pt.iscte.dcti.expressionsview.functionBuilder";


	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		Display.getDefault().syncExec(new Runnable() {
		    public void run() {
		    	ExpressionsView view = ExpressionsView.getInstance();
		    	
		    	if(view != null)
		    		view.refresh();
		    }
		});
		return null;
	}
	
	
}
