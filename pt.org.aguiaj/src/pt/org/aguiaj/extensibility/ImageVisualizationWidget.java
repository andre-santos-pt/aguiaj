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
package pt.org.aguiaj.extensibility;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import pt.org.aguiaj.common.AguiaJColor;
import pt.org.aguiaj.core.AguiaJActivator;

/**
 * Helper class to implement a visualization that is only based on images, depending on the state of the object.
 *
 * @param <T> The type of domain object that this widget renders.
 */
public abstract class ImageVisualizationWidget<T> extends VisualizationWidget.Adapter<T> {

	/**
	 * Returns the file name of the image associated with the given <code>object</code>
	 * 
	 * @param object Object. Contract: will be passed null.
	 * @return a string with the filename (without extension) located under a folder named "images" on the plugin root.
	 */
	protected abstract String getImageFile(T object);
	
	
	private Canvas imageCanvas;
	private String previousImage;
	private Image image;
	private boolean relayout;
	
	@Override
	public final void createSection(Composite section) {			
		section.setLayout(new RowLayout());
		imageCanvas = new Canvas(section, SWT.NONE);
		imageCanvas.setBackground(AguiaJColor.OBJECT.getColor());
		imageCanvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setBackground(AguiaJColor.OBJECT.getColor());
				e.gc.fillRectangle(e.gc.getClipping());
				if(image != null)
					e.gc.drawImage(image, 0, 0);				
			}
		});
		imageCanvas.setVisible(false);
		relayout = true;
	}

	
	@Override
	public void update(T object) {				
		imageCanvas.setVisible(object != null);

		if(object == null) {
			previousImage = null;
		}
		else {
			String file = getImageFile(object);
			if(file != null && file.equals(previousImage)) {
				relayout = false;
			}
			else if(file != null)  {
				image = AguiaJActivator.getDefault().getImageRegistry().get(file);	
				imageCanvas.setLayoutData(new RowData(image.getBounds().width, image.getBounds().height));	
				imageCanvas.update();
				imageCanvas.redraw();
				imageCanvas.layout();
				previousImage = file;
				relayout = true;
			}
		}
	}

	@Override
	public boolean needsRelayout() {
		return relayout;
	}
}
