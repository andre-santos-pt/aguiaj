package pt.org.aguiaj.console;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import aguiaj.console.Console;

import pt.org.aguiaj.extensibility.AguiaJHelper;

public class ShowConsoleAction implements IViewActionDelegate {

	private boolean on;
	
	static ShowConsoleAction instance;
	
	public ShowConsoleAction() {
		instance = this;
	}
	
	@Override
	public void run(IAction action) {
//		if(action.isChecked()) {
		if(!AguiaJHelper.existsReference("out"))
			AguiaJHelper.executeJavaInstruction("Console out = Console.getInstance()");
		else
			AguiaJHelper.executeJavaInstruction("Console.getInstance()");
		
//			AguiaJHelper.show("out");
//			on = true;
//		}
//		else {
//			AguiaJHelper.hide("out");
//			on = false;
//		}
	}

	public boolean isOn() {
		return on;
	}
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		
	}

}
