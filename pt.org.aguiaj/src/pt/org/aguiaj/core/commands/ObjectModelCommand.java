package pt.org.aguiaj.core.commands;

import pt.org.aguiaj.objects.ObjectModel;

public abstract class ObjectModelCommand implements Command {

	protected abstract void execute(ObjectModel model);
	
	@Override
	public final void execute() {
		execute(ObjectModel.getInstance());
	}
}
