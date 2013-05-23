/*******************************************************************************
 * Copyright (c) 2013 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.draw.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.extensibility.canvas.CanvasVisualizationWidget;
import pt.org.aguiaj.extensibility.canvas.DrawItem;
import pt.org.aguiaj.extensibility.canvas.LineDraw;
import pt.org.aguiaj.extensibility.canvas.RectangleDraw;
import pt.org.aguiaj.extensibility.canvas.TextDraw;
import aguiaj.draw.Grid;
import aguiaj.draw.Image;

public class GridWidget implements CanvasVisualizationWidget<Grid> {

	private static final int BORDER = 15;
	private int WIDTH = 33;

	private Grid grid;
	private Display display;

//	private Image[][] icons;
//	private Color[][] backgrounds;

	private int width;
	private int height;



	@Override
	public int canvasHeight() {
		return height;
	}

	@Override
	public int canvasWidth() { 
		return width;
	}

	@Override
	public void update(Grid grid) {	
		this.grid = grid;
		WIDTH = this.grid.getPositionWidth();
//		icons = new Image[grid.getNumberOfRows()][grid.getNumberOfColumns()];
//		backgrounds = new Color[grid.getNumberOfRows()][grid.getNumberOfColumns()];

		width = (grid.getNumberOfColumns() * (WIDTH)) + BORDER;
		height = (grid.getNumberOfRows() * (WIDTH)) + BORDER;
	}

	@Override
	public void initialize(final Canvas canvas) {
		display = canvas.getDisplay();
		
		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if(grid != null) {
					Rectangle positionArea = new Rectangle(BORDER, BORDER, grid.getNumberOfColumns() * WIDTH, grid.getNumberOfRows() * WIDTH); 
					if(positionArea.contains(e.x, e.y)) {
						int x = (e.x - BORDER) / WIDTH;
						int y = (e.y - BORDER) / WIDTH;
						String coord = "Position (" + y + ", " + x + ")";
						canvas.setToolTipText(coord);	
					}
					else {
						canvas.setToolTipText(null);
					}
				}
			}
		});

	}




	@Override
	public List<DrawItem> drawItems() {
		List<DrawItem> items = new ArrayList<DrawItem>();
		addLines(items);
		addPositions(items);
		return items;
	}





	private void addLines(List<DrawItem> items) {
		for(int x = BORDER; x < width; x += WIDTH)
			items.add(new LineDraw(new Point(x, 0), new Point(x, height)));

		for(int y = BORDER; y < height; y += WIDTH)
			items.add(new LineDraw(new Point(0, y), new Point(width, y)));

		for(int line = 0; line < grid.getNumberOfRows(); line++)
			items.add(new TextDraw(Integer.toString(line), new Point(2, BORDER + (line * WIDTH)), 7));
		
		for(int column = 0; column < grid.getNumberOfColumns(); column++)
			items.add(new TextDraw(Integer.toString(column), new Point(BORDER + (column * WIDTH)+3, 0), 7));
	}
	
	
	private void addPositions(List<DrawItem> items) {
		for(int row = 0; row < grid.getNumberOfRows(); row++) {
			for(int column = 0; column < grid.getNumberOfColumns(); column++) {
				aguiaj.draw.Color color = grid.getBackground(row, column);
				Color swtColor = new org.eclipse.swt.graphics.Color(display, color.getR(), color.getG(), color.getB());
				items.add(new RectangleDraw(BORDER + (column * WIDTH) + 1, BORDER + (row * WIDTH) + 1, WIDTH - 1, WIDTH - 1, swtColor));
				
				Image icon = grid.getImage(row, column);
				if(icon != null) {
					items.add(ImageWidget.createImageDraw(icon, new Point(BORDER + (column * WIDTH) + 1, BORDER + (row * WIDTH) + 1), 1));
				}
//				icons[row][column] = icon;
			}	
		}
	}
	
	
	

	//	@Override
	//	public void drawObject(GC gc) {
	//		if(image != null) {
	//			Common.drawImage(image, gc, 0, 0, zoom);
	//			for(int y = 0; y < image.getHeight(); y++) {
	//				for(int x = 0; x < image.getWidth(); x++) {				
	//					prev[y][x] = image.getColor(x, y);
	//				}
	//			}
	//		}
	//	}



	//	@Override
	//	public List<Rectangle> toRedraw() {
	//		if(image != null) {
	//			Point first = null;
	//			int lastX = 0;
	//			int lastY = 0;
	//			
	//			for(int y = 0; y < image.getHeight(); y++) {
	//				for(int x = 0; x < image.getWidth(); x++) {				
	//					if(!image.getColor(x, y).equals(prev[y][x])) {
	//						if(first == null)
	//							first = new Point(x, y);
	//						if(first.x > x)
	//							first.x = x;
	//						
	//						if(x > lastX)
	//							lastX = x;
	//						
	//						if(y > lastY)
	//							lastY = y;
	//					}	
	//				}
	//			}
	//
	//			toRedraw.clear();
	//			if(first != null) {
	//				Rectangle area = new Rectangle(first.x*zoom, first.y*zoom, (lastX - first.x + 1)*zoom, (lastY - first.y + 1)*zoom);
	//				toRedraw.add(area);
	//			}
	//		}
	//
	//		return toRedraw;
	//	}


}
