package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

public class RectangleFill extends DrawItem {
	final Color color;
	final int width;
	final int height;
	
	public RectangleFill(int x0, int y0, int width, int height, Color color) {
		super(x0, y0);
		this.color = color;
		this.width = width;
		this.height = height;
	}


	@Override
	public void draw(GC gc) {
		gc.setForeground(color);
		gc.drawRectangle(x, y, width, height);
	}
}