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
package pt.iscte.dcti.aguiaj;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;


public class AguiaJJavaPerspective implements IPerspectiveFactory {

	public static final String ID = "pt.iscte.dcti.aguiaj.javaperspective";

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(true);
		layout.setFixed(false);
//		String projExplorer = "org.eclipse.ui.navigator.ProjectExplorer";
		
		
		layout.addView(JavaUI.ID_PACKAGES,  IPageLayout.LEFT, 0.3f, editorArea);
		layout.getViewLayout(JavaUI.ID_PACKAGES).setMoveable(false);
		layout.getViewLayout(JavaUI.ID_PACKAGES).setCloseable(false);
		
		
//		layout.addView(ELearningView.ID, IPageLayout.BOTTOM, 0.7f, editorArea);
//		layout.addView(SlidesView.ID, IPageLayout.BOTTOM, 0.6f, JavaUI.ID_PACKAGES);
		
		
//		boolean isVisible = false; 
//		IEvaluationService service = (IEvaluationService) PlatformUI.getWorkbench().getService(IEvaluationService.class); 
//		IEvaluationContext appState = service.getCurrentState(); 
//		Object coolbar = appState.getVariable(ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME); 
//		if (coolbar instanceof Boolean) { 
//			isVisible = ((Boolean) coolbar).booleanValue(); 
//		} 
//
//		ActionFactory.IWorkbenchAction toggleToolbar = ActionFactory.TOGGLE_COOLBAR.create(PlatformUI.getWorkbench().getActiveWorkbenchWindow());
//		toggleToolbar.run(); 

		

	}

}
