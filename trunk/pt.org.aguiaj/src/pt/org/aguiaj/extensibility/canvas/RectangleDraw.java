package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class RectangleDraw extends DrawItem {
	final Color color;
	final int x1;
	final int y1;
	
	public RectangleDraw(int x0, int y0, int x1, int y1, Color color) {
		super(x0, y0);
		this.color = color;
		this.x1 = x1;
		this.y1 = y1;
	}


	@Override
	public void draw(GC gc) {
		gc.setBackground(color);
		gc.fillRectangle(x, y, x1, y1);
	}
}