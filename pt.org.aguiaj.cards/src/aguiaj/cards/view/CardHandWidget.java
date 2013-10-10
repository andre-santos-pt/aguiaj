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
package aguiaj.cards.view;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import pt.org.aguiaj.extensibility.AguiaJHelper;
import pt.org.aguiaj.extensibility.VisualizationWidget;
import aguiaj.cards.ICard;
import aguiaj.cards.CardHand;

public class CardHandWidget extends VisualizationWidget.Adapter<CardHand> {

	private Composite cardSection;
	private List<Composite> images;
	
	@Override
	public void createSection(Composite section) {
		section.setLayout(new FillLayout());
		cardSection = new Composite(section, SWT.BORDER);
		cardSection.setLayout(new RowLayout());
		images = new ArrayList<Composite>();
	}
	
	@Override
	public void update(CardHand hand) {
		for(Composite img : images)
			img.dispose();
		
		images.clear();
		
		if(hand.isEmpty()) {
			Composite imageComposite = new Composite(cardSection, SWT.NONE);
			imageComposite.setLayout(new RowLayout(SWT.VERTICAL));
			imageComposite.setBackground(cardSection.getBackground());
			Composite fill = new Composite(imageComposite, SWT.NONE);
			fill.setBackground(cardSection.getBackground());
			fill.setLayoutData(new RowData(CardWidget.WIDTH, CardWidget.HEIGHT));
			Label label = new Label(imageComposite, SWT.UNDERLINE_SINGLE);
			label.setText(" ");
			label.setBackground(cardSection.getBackground());
			images.add(imageComposite);
		}
		
		for(int i = 0; i < hand.getSize(); i++) {
			ICard card = hand.get(i);
			Composite imageComposite = new Composite(cardSection, SWT.NONE);
			imageComposite.setLayout(new RowLayout(SWT.VERTICAL));
			imageComposite.setBackground(cardSection.getBackground());
			
			Composite img = new Composite(imageComposite, SWT.NONE);
			img.setLayoutData(new RowData(CardWidget.WIDTH, CardWidget.HEIGHT));
			Image image = null;
			if(hand.isFlipped(i)) {
				image = AguiaJHelper.getPluginImage("CARD_BACK");
			}
			else {
				image = AguiaJHelper.getPluginImage(card.getRank().name() + "_" + card.getSuit().name());
			}
			img.setBackgroundImage(image);
			Label label = new Label(imageComposite, SWT.UNDERLINE_SINGLE);
			label.setText(hand.getText(i));
			label.setBackground(cardSection.getBackground());
			
			if(hand.isSelected(i)) {
				Color color = img.getDisplay().getSystemColor(SWT.COLOR_GRAY);
				imageComposite.setBackground(color);
				label.setBackground(color);
			}
			
			images.add(imageComposite);
		}
		
		cardSection.layout();
	}

	@Override
	public boolean needsRelayout() {
		return true;
	}
}
