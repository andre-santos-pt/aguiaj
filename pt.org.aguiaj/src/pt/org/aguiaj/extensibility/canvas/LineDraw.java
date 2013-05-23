package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

public class LineDraw extends DrawItem {
	final Point destiny;
	public LineDraw(Point origin, Point destiny) {
		super(origin.x, origin.y);
		this.destiny = destiny;
	}
	
	@Override
	public void draw(GC gc) {
		gc.drawLine(x, y, destiny.x, destiny.y);
	}

}