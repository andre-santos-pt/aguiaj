package pt.org.aguiaj.rcp;

import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import pt.org.aguiaj.extensibility.AguiaJContribution;

public class AguiaJWorkbenchAdvisor extends WorkbenchAdvisor {
	
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new AguiaJWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return AguiaJContribution.PERSPECTIVE;
	}
}
