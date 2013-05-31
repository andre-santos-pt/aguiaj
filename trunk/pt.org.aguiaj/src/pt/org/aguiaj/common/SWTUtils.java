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
package pt.org.aguiaj.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import pt.org.aguiaj.core.typewidgets.ExtensionTypeWidget;

public class SWTUtils {

	public static void showView(String id) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {			
			page.showView(id);			
		} 
		catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	public static void centerShell(Shell shell) {
		Rectangle bounds = shell.getParent().getBounds();
		Rectangle rect = shell.getBounds();
	    int x = bounds.x + (bounds.width - rect.width) / 2;
	    int y = bounds.y + (bounds.height - rect.height) / 2;
	    shell.setLocation(x, y);
	}
	
	public static void setColorRecursively(Control control, Color color) {
		boolean skip = 
			control.isDisposed() || 
			control instanceof Text || 
			control instanceof Group || 			
			control instanceof Canvas;
		
		if(!skip) {
			control.setBackground(color);
			if(control instanceof Composite) {
				Composite comp = (Composite) control;
				if(comp instanceof ExtensionTypeWidget) {
					((ExtensionTypeWidget) comp).paintWidget(color);					
				}
				else {
					for(Control c : ((Composite) control).getChildren())
						setColorRecursively(c, color);
				}
			}
		}
	}
	
	public static void setTooltipRecursively(Control control, String tooltip) {
		assert tooltip != null;
		
		if(control == null)
			return;
		
		boolean skip = 
			control.isDisposed() || 
			control instanceof Group || 			
			control instanceof Canvas;
		
		if(!skip) {
			control.setToolTipText(tooltip);
			if(control instanceof Composite) {
				for(Control c : ((Composite) control).getChildren())
					setTooltipRecursively(c, tooltip);
			}
		}
	}

	public static void showMessage(String header, String text, int icon) {
		MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), icon);
		messageBox.setText(header == null ? "" : header);
		messageBox.setMessage(text == null ? "" : text);
		messageBox.open();
	}
	
	public static void saveImageToFile(Control control, String defaultFileName) {
		GC gc = new GC(control);
		Image image = new Image(control.getDisplay(), control.getBounds().width, control.getBounds().height);
		gc.copyArea(image, 0, 0);
		gc.dispose();

		ImageData data = image.getImageData();
		ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[] {data};

		FileDialog fd = new FileDialog(control.getShell(), SWT.SAVE);
		fd.setText("Open");
		String[] filterExt = {"*.png","*.*" };
		fd.setFilterExtensions(filterExt);
		fd.setFileName(defaultFileName);
		
		String path = fd.open();

		if(path != null)
			loader.save(path, SWT.IMAGE_PNG);

		image.dispose();
	}
	
	
	
}
