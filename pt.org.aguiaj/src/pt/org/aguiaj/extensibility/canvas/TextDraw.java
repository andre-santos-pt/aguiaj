package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;


public class TextDraw extends DrawItem {
	final String text;
	final int size;
	
	public TextDraw(String text, Point point, int size) {
		super(point.x, point.y);
		this.text = text;
		this.size = size;
	}

	@Override	
	public void draw(GC gc) {
		gc.setFont(new Font(null, "Monospaced", size, SWT.NONE));
		gc.setForeground(new Color(null, 0, 0, 0));
		gc.drawText(text, x, y, SWT.DRAW_TRANSPARENT);
	}
}