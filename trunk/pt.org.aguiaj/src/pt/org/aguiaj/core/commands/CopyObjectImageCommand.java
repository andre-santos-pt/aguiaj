package pt.org.aguiaj.core.commands;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.common.SWTUtils;
import pt.org.aguiaj.objects.ReferenceStackWidget;


public class CopyObjectImageCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		 Control control = Display.getDefault().getCursorControl();
		 Composite parent = crawlUpReferencePair(control);
		 if(parent instanceof ReferenceStackWidget) {
			 SWTUtils.saveImageToFile(parent, ((ReferenceStackWidget) parent).getFirstReference() + ".png");
		 }
		return null;
	}

	private static ReferenceStackWidget crawlUpReferencePair(Control control) {
		if(control == null)
			return null;
		
		Composite parent = control.getParent();
		if(parent == null)
			return null;
		else if(parent instanceof ReferenceStackWidget)
			return (ReferenceStackWidget) parent;
		else
			return crawlUpReferencePair(parent);
			
	}
}