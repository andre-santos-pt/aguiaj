package pt.org.aguiaj.eclipse;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;



public class ImportPluginsContribution extends ExtensionContributionFactory {	

	@Override
	public void createContributionItems(IServiceLocator serviceLocator,
			IContributionRoot additions) {
		additions.addContributionItem(new ImportPluginsMenu(), null);
	}

	
}
