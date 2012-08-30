package pt.org.aguiaj.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.part.IntroPart;

public class AguiaJWelcomePage extends IntroPart {

	@Override
	public void standbyStateChanged(boolean standby) {
		
	}

	@Override
	public void createPartControl(Composite parent) {
				Composite comp = new Composite(parent, SWT.NONE);
				
		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.center = true;
		comp.setLayout(layout);
		comp.setBackground(white);
		
		Image image = new Image(Display.getDefault(), "images/aguiawecome.jpg");
		Composite imageComp = new Composite(comp, SWT.NONE);
		imageComp.setLayout(new FillLayout());
		imageComp.setBackgroundImage(image);
		imageComp.setLayoutData(new RowData(image.getBounds().width, image.getBounds().height));
		
		Link link = new Link(comp, SWT.NONE);
		FontData data = new FontData("Arial", 24, SWT.ITALIC);
		link.setText("<a>OK, let me go!</a>");
		link.setFont(new Font(Display.getDefault(), data));
		link.setBackground(white);
		link.addListener(SWT.Selection, new Listener () {
			public void handleEvent(Event event) {
				IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
				PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
			}
		});
		
//		Button button = new Button(comp, SWT.PUSH);
//		button.setText("OK, let me go!");		
//		button.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				IIntroPart introPart = PlatformUI.getWorkbench().getIntroManager().getIntro();
//				PlatformUI.getWorkbench().getIntroManager().closeIntro(introPart);
////				getIntroSite().getWorkbenchWindow().getActivePage().hideView(view)	
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				
//			}
//		});
		
//		Browser browser = new Browser(comp, SWT.NONE);
		
	}

	@Override
	public void setFocus() {
		
	}	
}
