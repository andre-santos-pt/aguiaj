package pt.org.aguiaj.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import aguiaj.console.Console;

import pt.org.aguiaj.extensibility.VisualizationWidget;

public class ConsoleView implements VisualizationWidget<Console> {

	private static final int LINES = 10;
	private Text textArea;
	
	@Override
	public void update(final Console console) {
		textArea.setText(content(console));
		textArea.setSelection(textArea.getText().length());
		textArea.setFont(new Font(textArea.getDisplay(), new FontData("Courier", 14, SWT.NONE)));
//		Menu menu = new Menu(textArea);
//		MenuItem item = new MenuItem(menu, SWT.PUSH);
//		item.setText("Clear");
//		item.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				console.clearLines();
//				textArea.setText("");
//			}
//		});
//		textArea.setMenu(menu);
	}
	
	private String content(Console console) {
		String s = "";
		for(int i = 0; i < console.getNumberOfLines(); i++)
			s += console.getLine(i) + "\n";
		
		return s;
	}

	@Override
	public void createSection(Composite parent) {
		parent.setLayout(new RowLayout());
		textArea = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		textArea.setLayoutData(new RowData(300, LINES * 18));
	}

	@Override
	public boolean needsRelayout() {
		return true;
	}

	@Override
	public Control getControl() {
		return textArea;
	}

}
