/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.images.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.CanvasVisualizationWidget;
import aguiaj.images.Image;
import aguiaj.images.contribution.Common;

public class ImageWidget implements CanvasVisualizationWidget<Image> {
	private static final int ZOOM_STEP = 3;
	
	private int zoom = 1;

	private Image image;
	private int width;
	private int height;
	private ArrayList<Rectangle> toRedraw = new ArrayList<Rectangle>(1);
	private aguiaj.colors.Color[][] prev;
	private Color background;
	
	private MenuItem zoomInItem;
	private MenuItem zoomOutItem;
	
	@Override
	public int canvasHeight() {
		return height * zoom;
	}

	@Override
	public int canvasWidth() { 
		return width * zoom;
	}

	@Override
	public void update(Image image) {				
		this.image = image;

		if(image != null) {
			width = image.getWidth(); 
			height = image.getHeight();

			if(prev == null)
				prev = new aguiaj.colors.Color[height][width];

			if(toRedraw.isEmpty())
				toRedraw.add(new Rectangle(0, 0, canvasWidth(), canvasHeight()));
		}		
	}

	@Override
	public void initialize(final Canvas canvas) {
		background = canvas.getParent().getBackground();
		canvas.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if(image != null) {
					String coord = "(" + e.x / zoom + ", " + e.y / zoom + ")";
					canvas.setToolTipText(coord);					
				}
			}
		});
		canvas.setBackground(background);
		createMenu(canvas);
		
	}
	
	
	
	private void createMenu(final Canvas canvas) {
		Menu menu = canvas.getMenu();
		if(menu == null)
			menu = new Menu(canvas);
		
		zoomInItem = new MenuItem(menu, SWT.PUSH);
		zoomInItem.setText("Zoom in");
		zoomInItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				zoom += ZOOM_STEP;
				toRedraw.clear();
				toRedraw.add(new Rectangle(0, 0, canvasWidth(), canvasHeight()));
				zoomOutItem.setEnabled(true);
				AguiaJHelper.updateObject(image);
			}

			
		});
		
		zoomOutItem = new MenuItem(menu, SWT.PUSH);
		zoomOutItem.setText("Zoom out");
		zoomOutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				zoom -= ZOOM_STEP;
				toRedraw.clear();
				toRedraw.add(new Rectangle(0, 0, canvasWidth(), canvasHeight()));
				if(zoom == 1)
					zoomOutItem.setEnabled(false);
				
				AguiaJHelper.updateObject(image);
			}
		});
		zoomOutItem.setEnabled(false);
	}

	



	@Override
	public void drawObject(GC gc) {
		if(image != null) {
			Common.drawImage(image, gc, 0, 0, zoom);
			for(int y = 0; y < image.getHeight(); y++) {
				for(int x = 0; x < image.getWidth(); x++) {				
					prev[y][x] = image.getColor(x, y);
				}
			}
		}
	}



	@Override
	public List<Rectangle> toRedraw() {
		if(image != null) {
			Point first = null;
			int lastX = 0;
			int lastY = 0;
			
			for(int y = 0; y < image.getHeight(); y++) {
				for(int x = 0; x < image.getWidth(); x++) {				
					if(!image.getColor(x, y).equals(prev[y][x])) {
						if(first == null)
							first = new Point(x, y);
						if(first.x > x)
							first.x = x;
						
						if(x > lastX)
							lastX = x;
						
						if(y > lastY)
							lastY = y;
					}	
				}
			}

			toRedraw.clear();
			if(first != null) {
				Rectangle area = new Rectangle(first.x*zoom, first.y*zoom, (lastX - first.x + 1)*zoom, (lastY - first.y + 1)*zoom);
				toRedraw.add(area);
			}
		}

		return toRedraw;
	}
}
