package aguiaj.draw.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;

import pt.org.aguiaj.extensibility.canvas.CanvasVisualizationWidget;
import pt.org.aguiaj.extensibility.canvas.DrawItem;
import pt.org.aguiaj.extensibility.canvas.RectangleDraw;
import pt.org.aguiaj.extensibility.canvas.RectangleFill;
import pt.org.aguiaj.extensibility.canvas.TextDraw;
import aguiaj.draw.IColor;

public class RGBColorWidget implements CanvasVisualizationWidget<IColor>{

	private static Color BLACK =  new Color(null, 0, 0, 0);
	private Color swtColor;

	private List<DrawItem> list;
	
	public RGBColorWidget() {
		list = new ArrayList<DrawItem>(4);
	}
	
	@Override
	public void update(IColor color) {
		swtColor = new Color(null, color.getR(), color.getG(), color.getB());
		list.clear();
		list.add(new RectangleFill(0, 0, 50, 50, swtColor));
		list.add(new RectangleDraw(0, 0, 50, 49, BLACK));
		list.add(new TextDraw("R: " + color.getR(), new Point(55, 0), 12));
		list.add(new TextDraw("G: " + color.getG(), new Point(55, 15), 12));
		list.add(new TextDraw("B: " + color.getB(), new Point(55, 30), 12));
	}

	@Override
	public void initialize(Canvas canvas) {
		
	}

	@Override
	public int canvasWidth() {
		return 150;
	}

	@Override
	public int canvasHeight() {
		return 50;
	}

	@Override
	public List<DrawItem> drawItems() {	
		return list;
	}

}
