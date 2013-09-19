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
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.canvas.CanvasVisualizationWidget;
import pt.org.aguiaj.extensibility.canvas.DrawItem;
import pt.org.aguiaj.extensibility.canvas.ImageDraw;
import pt.org.aguiaj.extensibility.contracts.ContractDecorator;
import aguiaj.draw.IDimension;
import aguiaj.draw.IImage;
import aguiaj.draw.ITransparentImage;
import aguiaj.draw.contracts.ImageContract;

public class ImageWidget implements CanvasVisualizationWidget<IImage> {
	private static final int ZOOM_STEP = 3;
	
	private int zoom = 1;

	private ImageContract image;
	private int width;
	private int height;
	private aguiaj.draw.IColor[][] prev;
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
	public void update(IImage image) {				
		this.image = new ImageContract(image);

		if(image != null) {
			IDimension dim = image.getDimension();
			width = dim.getWidth(); 
			height = dim.getHeight();

			if(prev == null)
				prev = new aguiaj.draw.IColor[height][width];
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
				zoomOutItem.setEnabled(true);
				AguiaJHelper.updateObject(image);
			}
		});
		
		zoomOutItem = new MenuItem(menu, SWT.PUSH);
		zoomOutItem.setText("Zoom out");
		zoomOutItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				zoom -= ZOOM_STEP;
				if(zoom == 1)
					zoomOutItem.setEnabled(false);
				
				AguiaJHelper.updateObject(image);
			}
		});
		zoomOutItem.setEnabled(false);
	}

	
	
	private List<DrawItem> single = new ArrayList<DrawItem>(1);
	
	@Override
	public List<DrawItem> drawItems() {
		single.clear();
		single.add(createImageDraw(image, image.getWrappedObject() instanceof ITransparentImage, new Point(0,0), zoom));
		return single;
	}

	static ImageDraw createImageDraw(ImageContract image, boolean transparency, Point origin, int zoom) {
		PaletteData palette = new PaletteData(0xFF0000, 0x00FF00, 0x0000FF);
		IDimension dim = image.getDimension();
		int width = dim.getWidth();
		int height = dim.getHeight();
		ImageData data = new ImageData(width, height, 24, palette);
		data.alpha = -1;
		byte[] alpha = new byte[width*height];
		int[] v = new int[width*height];
		int i = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				aguiaj.draw.IColor pixel = image.getColor(x, y);
				v[i] = palette.getPixel(new RGB(pixel.getR(), pixel.getG(), pixel.getB()));
				if(transparency) {
					int t = ((ITransparentImage)image.getWrappedObject()).getOpacity(x, y);
					alpha[i] = (byte) ((t*255)/100);
				}
				else {
					alpha[i] = (byte) 255;
				}
				i++;
			}
		}

		data.setPixels(0, 0, v.length, v, 0); 
		data.setAlphas(0, 0, alpha.length, alpha, 0); 
		
		data = data.scaledTo(width*zoom, height*zoom);
		
		return new ImageDraw(data, origin);
	}
	
}
