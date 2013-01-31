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


import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import pt.org.aguiaj.core.AguiaJActivator;


public enum AguiaJImage {	
	NULL("null.gif"),
	NULL_SMALL("null_small.gif"),
	
	REFARROW("arrow.jpg"),
	DELETE("delete.gif"),
	DELETEDEAD("trash.gif"),
	REFRESH("refresh.gif"),
	IMPORTED_PACKAGE("imported_package.gif"),
	PACKAGE("package.gif"),
	INSPECT("inspect.gif"),
	QUESTION("question.gif"),
	QUESTION_BIG("question_big.gif"),
	ERROR("error.gif"),	
	MAIL("mail.gif"),		
	PENCIL("pencil.gif"),
	NA("na.png"),
	
	// default
	CUBE("cube.gif"),
	DIAMOND("diamond.gif"),
	MOON("moon.gif"),
	SNOW("snow.gif"),
	STAR("star.gif"),
	TARGET("target.gif");
			
	private static final AguiaJImage[] TYPE_ICONS = { CUBE, DIAMOND, MOON, SNOW, STAR, TARGET };  
	private static final String IMAGE_FOLDER = "images";
	private IPath path;
	private static int nextTypeIconIndex;
	
	static {
		nextTypeIconIndex = 0;
	}
	
	private AguiaJImage(String fileName) {		
		path = new Path(IMAGE_FOLDER).append(fileName);
	}
	

	
	public static AguiaJImage nextTypeIcon() {
		if(nextTypeIconIndex == TYPE_ICONS.length) {
			return QUESTION;
		}
		else {
			return TYPE_ICONS[nextTypeIconIndex++];			
		}
			
	}

	public Image getImage() {
		AbstractUIPlugin plugin = AguiaJActivator.getDefault();
		ImageRegistry imageRegistry = plugin.getImageRegistry();		
		return imageRegistry.get(name());
	}
	
	public ImageDescriptor getImageDescriptor() {
		AbstractUIPlugin plugin = AguiaJActivator.getDefault();
		ImageRegistry imageRegistry = plugin.getImageRegistry();		
		return imageRegistry.getDescriptor(name());
	}
	
	public IPath getPath() {
		return path;
	}
	
	public String getId() {
		return name();
	}
	
}
