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
package pt.org.aguiaj.core;

import static pt.org.aguiaj.extensibility.AguiaJContribution.CLASSES_VIEW;
import static pt.org.aguiaj.extensibility.AguiaJContribution.DOCUMENTATION_VIEW;
import static pt.org.aguiaj.extensibility.AguiaJContribution.HISTORY_VIEW;
import static pt.org.aguiaj.extensibility.AguiaJContribution.JAVABAR_VIEW;
import static pt.org.aguiaj.extensibility.AguiaJContribution.OBJECTS_VIEW;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
public class AguiaJPerspective implements IPerspectiveFactory {

	
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);
				
		layout.addView(CLASSES_VIEW,  IPageLayout.LEFT, 0.7f, editorArea);
				
		IFolderLayout objectsFolder = 
			layout.createFolder("objectsFolder", IPageLayout.RIGHT, 0.3f, CLASSES_VIEW);
		
		IFolderLayout javaBarFolder = 
			layout.createFolder("javabarFolder", IPageLayout.BOTTOM, 0.8f, "objectsFolder");
		
		objectsFolder.addView(OBJECTS_VIEW);
		
		javaBarFolder.addView(JAVABAR_VIEW);		
		javaBarFolder.addView(HISTORY_VIEW);
		javaBarFolder.addView(DOCUMENTATION_VIEW);	

		//layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM, 0.8f, CLASSES_VIEW);
		
		block(layout, OBJECTS_VIEW);
		block(layout, CLASSES_VIEW);
	}

	
	private void block(IPageLayout layout, String viewId) {
		layout.getViewLayout(viewId).setCloseable (false);
		layout.getViewLayout(viewId).setMoveable(false);
	}
}
