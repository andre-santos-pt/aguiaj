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
package aguiaj.grid.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

import pt.org.aguiaj.extensibility.CanvasVisualizationWidget;
import aguiaj.colors.Color;
import aguiaj.grid.Grid;
import aguiaj.grid.Position;
import aguiaj.images.Image;
import aguiaj.images.contribution.Common;

public class GridWidget implements CanvasVisualizationWidget<Grid>  {
	private static final int BORDER = 15;
	private static final int SIDE = 33;
	
	private Grid grid;

	private Image[][] icons;
	private Color[][] backgrounds;

	private int width;
	private int height;
	
	private Display display;
	private Font fontIndex;

	@Override
	public void initialize(final Canvas canvas) {
		display = canvas.getDisplay();
		fontIndex = new Font(display, "Monospaced", 10, SWT.NONE);

		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if(grid != null) {
					Rectangle positionArea = new Rectangle(BORDER, BORDER, grid.getNumberOfColumns() * SIDE, grid.getNumberOfRows() * SIDE); 
					if(positionArea.contains(e.x, e.y)) {
						int x = (e.x - BORDER) / SIDE;
						int y = (e.y - BORDER) / SIDE;
						String coord = "Position (" + y + ", " + x + ")";
						canvas.setToolTipText(coord);	
					}
					else {
						canvas.setToolTipText(null);
					}
				}
			}
		});
		
		canvas.addDragDetectListener(new DragDetectListener() {
			
			@Override
			public void dragDetected(DragDetectEvent e) {
System.out.println("DRAG - " + e.x + " " + e.y);				
			}
		});
	}

	@Override
	public void update(Grid grid) {
		
		if(grid != this.grid) {
			this.grid = grid;
			if(grid != null) {
				icons = new Image[grid.getNumberOfRows()][grid.getNumberOfColumns()];
				backgrounds = new Color[grid.getNumberOfRows()][grid.getNumberOfColumns()];
			}
		}

		if(this.grid != null) {
			width = (grid.getNumberOfColumns() * (SIDE)) + 1 + BORDER;
			height = (grid.getNumberOfRows() * (SIDE)) + 1 + BORDER;
		}
	}

	@Override
	public int canvasHeight() {
		return height;
	}

	@Override
	public int canvasWidth() {
		return width;
	}

	@Override
	public List<Rectangle> toRedraw() {
		List<Rectangle> redrawAreas = new ArrayList<Rectangle>();
		if(grid != null) {
			for(int line = 0; line < grid.getNumberOfRows(); line++) {
				for(int column = 0; column < grid.getNumberOfColumns(); column++) {
					Position p = grid.getPosition(line, column);
					Image icon = p.getIcon();
					boolean changes =
						icon != null && !icon.equals(icons[line][column]) ||
						icon == null && icons[line][column] != null ||
						!p.getBackground().equals(backgrounds[line][column]);

					if(changes) {
						Rectangle rect = new Rectangle(BORDER + (column*SIDE)+1, BORDER + (line*SIDE)+1, SIDE-1, SIDE-1);
						redrawAreas.add(rect);
					}
				}
			}
		}
		return redrawAreas;
	}

	@Override
	public void drawObject(GC gc) {
		assert grid != null;

		drawLines(gc, display);

		for(int line = 0; line < grid.getNumberOfRows(); line++) {
			for(int column = 0; column < grid.getNumberOfColumns(); column++) {
				Position p = grid.getPosition(line, column);
				drawBackground(gc, display, line, column, p);								
				Image icon = p.getIcon();
				if(icon != null) {
					Common.drawImage(icon, gc, BORDER + (column * SIDE) + 1, BORDER + (line * SIDE) + 1, 1);					
				}
				icons[line][column] = icon;
			}	
		}
	}

	private void drawBackground(GC gc, Display display, int line, int column, Position p) {
		Color bgcolor = p.getBackground();
		backgrounds[line][column] = bgcolor;
		RGB rgb = new RGB(bgcolor.getR(), bgcolor.getG(), bgcolor.getB());
		gc.setBackground(new org.eclipse.swt.graphics.Color(display, rgb));
		gc.fillRectangle(BORDER + (column * SIDE) + 1, BORDER + (line * SIDE) + 1, SIDE - 1, SIDE - 1);
	}


	private void drawLines(GC gc, Display display) {
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));

		gc.drawLine(0, 0, 0, height);
		gc.drawLine(0, 0, width, 0);
		
		for(int x = BORDER; x < width; x += SIDE)
			gc.drawLine(x, 0, x, height);

		for(int y = BORDER; y < height; y += SIDE)
			gc.drawLine(0, y, width, y);

		for(int line = 0; line < grid.getNumberOfRows(); line++) {
			gc.setFont(fontIndex);
			gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.drawText(Integer.toString(line), 2, BORDER + (line * SIDE), SWT.DRAW_TRANSPARENT);
		}
		
		for(int column = 0; column < grid.getNumberOfColumns(); column++) {
			gc.setFont(fontIndex);
			gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_GRAY));
			gc.drawText(Integer.toString(column),  BORDER + (column * SIDE)+3, 0, SWT.DRAW_TRANSPARENT);
		}
	}
}
