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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.extensibility.canvas.CanvasVisualizationWidget;
import pt.org.aguiaj.extensibility.canvas.DrawItem;
import pt.org.aguiaj.extensibility.canvas.LineDraw;
import pt.org.aguiaj.extensibility.canvas.RectangleFill;
import pt.org.aguiaj.extensibility.canvas.TextDraw;
import aguiaj.draw.IGrid;
import aguiaj.draw.IImage;
import aguiaj.draw.ITransparentImage;
import aguiaj.draw.contracts.ColorContract;
import aguiaj.draw.contracts.GridContract;
import aguiaj.draw.contracts.ImageContract;

public class GridWidget implements CanvasVisualizationWidget<IGrid> {

	private static final int BORDER = 15;
	private int WIDTH = 33;

	private IGrid grid;
	private Display display;

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
	public void update(IGrid grid) {	
		this.grid = new GridContract(grid);
		WIDTH = this.grid.getPositionPixels();

		width = (this.grid.getDimension().getWidth() * (WIDTH)) + BORDER;
		height = (this.grid.getDimension().getHeight() * (WIDTH)) + BORDER;
	}

	@Override
	public void initialize(final Canvas canvas) {
		display = canvas.getDisplay();
		
		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if(grid != null) {
					Rectangle positionArea = new Rectangle(BORDER, BORDER, grid.getDimension().getWidth() * WIDTH, grid.getDimension().getHeight() * WIDTH); 
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

		for(int line = 0; line < grid.getDimension().getHeight(); line++)
			items.add(new TextDraw(Integer.toString(line), new Point(2, BORDER + (line * WIDTH)), 7));
		
		for(int column = 0; column < grid.getDimension().getWidth(); column++)
			items.add(new TextDraw(Integer.toString(column), new Point(BORDER + (column * WIDTH)+3, 0), 7));
	}
	
	
	private void addPositions(List<DrawItem> items) {
		for(int row = 0; row < grid.getDimension().getHeight(); row++) {
			for(int column = 0; column < grid.getDimension().getWidth(); column++) {
				aguiaj.draw.IColor color = new ColorContract(grid.getBackground(row, column));
				Color swtColor = new org.eclipse.swt.graphics.Color(display, color.getR(), color.getG(), color.getB());
				items.add(new RectangleFill(BORDER + (column * WIDTH) + 1, BORDER + (row * WIDTH) + 1, WIDTH - 1, WIDTH - 1, swtColor));
				
				IImage icon = grid.getImageAt(row, column);
				if(icon != null) {
					items.add(ImageWidget.createImageDraw(new ImageContract(icon), icon instanceof ITransparentImage, new Point(BORDER + (column * WIDTH) + 1, BORDER + (row * WIDTH) + 1), 1));
				}
			}	
		}
	}
	
}
