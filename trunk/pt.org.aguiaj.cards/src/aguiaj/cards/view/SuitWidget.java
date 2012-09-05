/*******************************************************************************
 * Copyright (c) 2012 Andr� L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andr� L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.cards.view;

import pt.org.aguiaj.extensibility.ImageVisualizationWidget;
import aguiaj.cards.Suit;

public class SuitWidget extends ImageVisualizationWidget<Suit> {
	
//	static final int SIDE = 42;
	
	@Override
	protected String getImageFile(Suit suit) {
		return suit.name();
	}	
}
