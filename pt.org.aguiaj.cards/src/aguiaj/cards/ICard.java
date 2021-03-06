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
package aguiaj.cards;


/**
 * Represents a playing card.
 * 
 * @author Andre L. Santos
 */
public interface ICard {
	/**
	 * Card rank.
	 * @return a non-null reference
	 */
	Rank getRank();
	
	/**
	 * Card suit.
	 * @return a non-null reference
	 */
	Suit getSuit();
}
