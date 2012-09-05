/*******************************************************************************
 * Copyright (c) 2012 André L. Santos.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     André L. Santos - initial API and implementation
 ******************************************************************************/
package aguiaj.cards.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aguiaj.cards.Card;
import aguiaj.cards.CardStack;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.VisualizationWidget;

public class CardStackWidget 
implements VisualizationWidget<CardStack> {

	private Composite section;
	private Card top;
	private boolean relayout;
	
	@Override
	public void update(CardStack stack) {
		if(!stack.isEmpty()) {
			Card card = stack.topCard();
			if(!card.equals(top)) {
				top = card;
				relayout = true;
				Image img = AguiaJHelper.getPluginImage(top.rank.name() + "_" + top.suit.name());
				section.setBackgroundImage(img);
			}
			else {
				relayout = false;
			}			
		}
		else {
			if(top != null) {
				relayout = true;
				top = null;
			}			
			section.setBackgroundImage(null);
		}
	}

	@Override
	public void createSection(Composite parent) {
		parent.setLayout(new RowLayout());
		section = new Composite(parent, SWT.BORDER);
		section.setLayoutData(new RowData(CardWidget.WIDTH, CardWidget.HEIGHT));
		section.setLayout(new FillLayout());
	}

	@Override
	public boolean needsRelayout() {
		return relayout;
	}

	@Override
	public Control getControl() {		
		return section;
	}
}
