/*******************************************************************************
 * Copyright (c) 2014 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L Santos - developer
 ******************************************************************************/
package aguiaj.iscte.contribution;

import pt.org.aguiaj.extensibility.ImportItemProvider;
import aguiaj.iscte.ColorImage;
import aguiaj.iscte.ImageUtilsIscte;

public class ColorImageProvider implements ImportItemProvider {

	@Override
	public String getInstruction(String filePath) {
		return ImageUtilsIscte.class.getSimpleName().concat(".loadColorImage(\"").concat(filePath).concat("\")");
	}

	@Override
	public Class<ColorImage> getType() {
		return ColorImage.class;
	}
	
}
