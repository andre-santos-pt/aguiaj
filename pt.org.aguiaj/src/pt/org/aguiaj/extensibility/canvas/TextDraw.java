package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import pt.org.aguiaj.common.AguiaJColor;


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
		Font font = new Font(gc.getDevice(), "Courier", size, SWT.NONE);
		gc.setFont(font);
		gc.setForeground(AguiaJColor.BLACK.getColor());
		gc.drawText(text, x, y, SWT.DRAW_TRANSPARENT);
		font.dispose();
	}
}