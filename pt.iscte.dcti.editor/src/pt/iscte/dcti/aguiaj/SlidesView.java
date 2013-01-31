package pt.iscte.dcti.aguiaj;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class SlidesView extends ViewPart {

	public static final String ID = "pt.iscte.dcti.aguiaj.slides";

	@Override
	public void createPartControl(Composite parent) {
		Browser browser = new Browser(parent, SWT.NONE);
		browser.setUrl("https://docs.google.com/present/view?id=dfbm24wb_230g6fz99hk");
	}

	@Override
	public void setFocus() {

	}

}
