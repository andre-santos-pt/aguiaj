/*******************************************************************************
 * Copyright (c) 2011 Andre L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Andre L. Santos - concept inventor, architect, developer
 ******************************************************************************/
package aguiaj.cards.view;

import pt.org.aguiaj.extensibility.ImageVisualizationWidget;
import aguiaj.cards.Card;

public class CardWidget extends ImageVisualizationWidget<Card> {
	static final int WIDTH = 80;
	static final int HEIGHT = 100;
	
	@Override
	protected String getImageFile(Card card) {
		return card.rank.name() + "_" + card.suit.name();	
	}	
}
