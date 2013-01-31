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

import java.util.ArrayList;
import java.util.List;


/**
 * Represents card stacks, which can be displayed according to different modes:
 * all cards hidden, only the top card visible, all cards shown.
 * 
 * @author Andre L. Santos
 */
public class CardStack {
	
	private final List<Card> stack;
	
	/**
	 * Constructs an empty card stack.
	 * 
	 * @param mode Display mode
	 */
	public CardStack() {
		stack = new ArrayList<Card>();
	}
	
	/**
	 * Number of cards in the stack.
	 */
	public int getSize() {
		return stack.size();
	}
	
	/**
	 * Is the stack empty?
	 */
	public boolean isEmpty() {
		return getSize() == 0;
	}

	/**
	 * Obtain the card that is on top of the stack.
	 * @return The top card, or <code>null</code> if the stack is empty
	 */
	public Card topCard() {
		if(stack.isEmpty())
			throw new IllegalStateException("The stack is empty");
		
		return stack.get(stack.size()-1);
	}
	
	/**
	 * Obtain all cards of the stack, ordered from the bottom-most to 
	 * the top-most of card of the stack.
	 * 
	 * @return An array of cards with length equal to the number of cards of the stack
	 */
	public Card[] allCards() {
		return stack.toArray(new Card[getSize()]);
	}
	
	/**
	 * Put a card on top of the stack.
	 * 
	 * @param card The card to be placed.
	 */
	public void put(Card card) {
		if(card == null)
			throw new NullPointerException("Argument cannot be null");
		
		stack.add(card);
	}
	
	/**
	 * Remove the top card of the stack.
	 * 
	 * @return The top card.
	 * @throws IllegalStateException if the stack is empty
	 */
	public Card pick() {
		if(stack.isEmpty())
			throw new IllegalStateException("The stack is empty");
		
		Card top = topCard();
		stack.remove(stack.size()-1);
		return top;
	}
}
