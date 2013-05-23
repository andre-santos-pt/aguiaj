package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.graphics.GC;

public abstract class DrawItem {
	protected final int x;
	protected final int y;

	protected DrawItem(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract void draw(GC gc);
}