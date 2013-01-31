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
package aguiaj.images.contribution;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import aguiaj.images.ImageUtils;

public class ImportImageAction implements IViewActionDelegate {
	private IViewPart view;
	private enum ImageType {
		BINARY("loadBinaryImage") {
			public String toString() {
				return "Binary";
			}			 
		}, 
		BLACKWHITE("loadBlackWhiteImage") {
			public String toString() {				
				return "Grayscale";
			}
		}, 
		COLOR("loadColorImage") {
			public String toString() {
				return "Color";
			}			 
		};
		
		public final String methodName;
		
		private ImageType(String methodName) {
			this.methodName = methodName;
		}
	}
	
	@Override
	public void run(IAction action) {
		FileDialog fd = new FileDialog(view.getViewSite().getShell(), SWT.OPEN);
		fd.setText("Open");
		String[] filterExt = { "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.*" };
		fd.setFilterExtensions(filterExt);			
		String path = fd.open();
		//if(path != null)
		//	AguiaJHelper.executeJavaInstruction("ImageUtils.loadColorImage(\"" + path + "\")");
		
		if(path != null) {
			ElementListSelectionDialog dialog = new ElementListSelectionDialog(view.getViewSite().getShell(), new LabelProvider());
			dialog.setElements(ImageType.values());
			dialog.setTitle("Import image as...");
			if (dialog.open() == Window.OK) {
				ImageType type = (ImageType) dialog.getFirstResult();				
				if(path != null) {
					AguiaJHelper.executeJavaInstruction(ImageUtils.class.getSimpleName() + "." + 
							type.methodName + "(\"" + path + "\")");					
				}
//				AguiaJHelper.addObject(img);	
			}
		}
	}

	
	
	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public void init(IViewPart view) {
		this.view = view;
	}
}
