package pt.org.aguiaj.extensibility.canvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;

public class ImageDraw extends DrawItem {
	final ImageData data;
	
	public ImageDraw(ImageData data, Point location) {
		super(location.x, location.y);
		this.data = data;
	}

	@Override
	public void draw(GC gc) {
		Image img = new Image(gc.getDevice(), data);
		gc.drawImage(img, x, y);
		img.dispose();
	}	
}