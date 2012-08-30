package pt.org.aguiaj.rcp;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class AguiaJActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
	private IWorkbenchAction aboutAction;
//	private IAction changeWorkingDirAction;
	private IWorkbenchAction preferencesAction;
	private IWorkbenchAction introAction;
//	public static IWorkbenchAction resetPerspectiveAction;

	public AguiaJActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file.
		// Registering also provides automatic disposal of the actions when
		// the window is closed.

		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);

		aboutAction = ActionFactory.ABOUT.create(window);
		register(aboutAction);

//		changeWorkingDirAction = new Action("Change working directory") {
//
//			public String getId(){
//				return "change workingdir";				
//			}
//
//			public void run() { 
//				try {
//					new ChangeWorkingDirCommand().execute(null);
//					resetPerspective();
//				} catch (ExecutionException e) {					
//					e.printStackTrace();
//				}
//			}
//		};
//		register(changeWorkingDirAction);


		preferencesAction = ActionFactory.PREFERENCES.create(window);
//		introAction = ActionFactory.INTRO.create(window);
//		resetPerspectiveAction = new ResetPerspectiveNoDialogAction(window);
	}

	protected void fillMenuBar(IMenuManager menuBar) {
		MenuManager fileMenu = new MenuManager("&File",
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);

//		fileMenu.add(changeWorkingDirAction);
		fileMenu.add(preferencesAction);
		fileMenu.add(new Separator());
		fileMenu.add(exitAction);
		

		MenuManager helpMenu = new MenuManager("&Help",
				IWorkbenchActionConstants.M_HELP);

		helpMenu.add(aboutAction);
		helpMenu.add(new Separator());
//		helpMenu.add(introAction);
		menuBar.add(helpMenu);


	}

//	public static void resetPerspective() {
//		if(resetPerspectiveAction != null)
//			resetPerspectiveAction.run();
//	}

}
