package pt.org.aguiaj.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import pt.org.aguiaj.common.widgets.LabelWidget;

public class CompositeFrame {

	
	public static Composite create(Composite parent, final String title) {
		Composite comp = new Composite(parent, SWT.BORDER);
		comp.setLayout(new RowLayout(SWT.VERTICAL));
		
		LabelWidget label = new LabelWidget.Builder().small().bold().text(title).create(comp);
		
//		addPaintListener(new PaintListener() {
//			@Override
//			public void paintControl(PaintEvent e) {
//				e.gc.drawText(title, 5, 0);
//			}
//		});
		
		Composite content = new Composite(comp, SWT.NONE);
		return content;
	}
	
	
	
}
