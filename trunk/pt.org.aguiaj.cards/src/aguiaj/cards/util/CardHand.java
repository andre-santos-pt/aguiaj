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
package aguiaj.cards.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import aguiaj.cards.ICard;
import aguiaj.cards.Rank;
import aguiaj.cards.Suit;

//TODO equals/hashcode

/**
 * Represents card hands, composed of zero or more ordered cards.
 * 
 * @author Andre L. Santos
 */
public class CardHand {
	private final List<ICard> cards;
	private final Set<Integer> selected;
	private final Set<Integer> flipped;
	private final Map<Integer, String> cardsText;
	
	/**
	 * Constructs an empty card hand.
	 */
	public CardHand() {
		cards = new ArrayList<ICard>();
		selected = new HashSet<Integer>();
		flipped = new HashSet<Integer>();
		cardsText = new HashMap<Integer, String>();
	}
	
	/**
	 * Constructs a card hand using the cards contained in the array <i>cards</i>.
	 * If the array has null elements, these are ignored.
	 * 
	 * @param cards Array of cards to populate the card hand
	 */
	public CardHand(ICard[] cards) {
		this();
		
		if(cards == null)
			throw new NullPointerException("Argument cannot be null");
				
		for(ICard c : cards)
			if(c != null)
				add(c);
	}
	
	/**
	 * Creates a random card hand with a given cardinality.
	 * Duplicate cards may exist.
	 * 
	 * @param numberOfCards Number of cards
	 */
	public static CardHand random(int numberOfCards) {
		if(numberOfCards < 0)
			throw new IllegalArgumentException("Number must be greater or equal to zero");
		
		CardHand random = new CardHand();
		for(int i = 0; i != numberOfCards; i++)
			random.add(randomCard());
		
		return random;
	}
	
	private void checkNotNull(Object obj) {
		if(obj == null)
			throw new NullPointerException("Argument cannot be null");
	}
	
	private void checkIndex(int index) {
		if(index < 0 || index >= cards.size())
			throw new IndexOutOfBoundsException("Invalid index");
	}
	
	
	
	/**
	 * Is empty?
	 * 
	 * @return <code>true</code> if the hand is empty, <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	/**
	 * Number of cards.
	 * 
	 * @return The number of cards that compose the card hand
	 */
	public int getSize() {
		return cards.size();
	}

	
	/**
	 * Boolean query to check if a card exists in the card hand.
	 * 
	 * @param card Card to search for
	 * @return <code>true</code> if the card hand contains the card, <code>false</code> otherwise 
	 */
	public boolean contains(ICard card) {
		checkNotNull(card);
		
		return cards.contains(card);
	}
	
	/**
	 * Obtains a card.
	 * 
	 * @param index Index of the card
	 * 
	 * @return The card at the given index
	 */
	public ICard get(int index) {
		checkIndex(index);
		
		return cards.get(index);		
	}
	
	/**
	 * Adds a card to the end of the card hand (right).
	 * Duplicates are allowed.
	 * 
	 * @param card Card to be added
	 */
	public void add(ICard card) {
		checkNotNull(card);
		
		cards.add(card);
	}
	
	/**
	 * Adds a card at a specific index.
	 *  
	 * @param card Card to be added
	 * @param index Index where to insert the card
	 */
	public void addAt(ICard card, int index) {
		checkNotNull(card);
		
		if(index != cards.size())
			checkIndex(index);
		
		if(index == cards.size())
			cards.add(card);
		else
			cards.add(index, card);
	}
	

	/**
	 * Flips a card.
	 * 
	 * @param index Index of the card
	 */
	public void flip(int index) {
		checkIndex(index);
		
		if(flipped.contains(index))
			flipped.remove(index);
		else
			flipped.add(index);
	}
	
	/**
	 * Boolean query to check if a card is flipped
	 * 
	 * @param index Index of the card
	 * @return <code>true</code> if the card is flipped, <code>false</code> otherwise
	 */
	public boolean isFlipped(int index) {
		checkIndex(index);

		return flipped.contains(index);
	}
	
	/**
	 * Defines the text displayed below a card.
	 * 
	 * @param index Index of the card
	 * @param text Text to display
	 */
	public void setText(int index, String text) {
		checkIndex(index);
		checkNotNull(text);
		
		if(text == null)
			cardsText.remove(index);
		else
			cardsText.put(index, text);
	}
	
	/**
	 * Obtains the text that is displayed below a card.
	 * 
	 * @param index Index of the card
	 * @return Displayed text 
	 */
	public String getText(int index) {
		checkIndex(index);
		
		String text = cardsText.get(index);
		return text != null ? text : Integer.toString(index);
	}
		
	/**
	 * Selects a card.
	 * 
	 * @param index Index of the card
	 */
	public void select(int index) {
		checkIndex(index);
		
		selected.add(index);
	}
	
	// getSelection
	//REVER
	/**
	 * Boolean query to check if a card is selected.
	 * 
	 * @param index Index of the card
	 * @return <code>true</code> if the card is selected, <code>false</code> otherwise
	 */
	public boolean isSelected(int index) {
		checkIndex(index);
		
		return selected.contains(index);
	}
	
	/**
	 * Unselects every selected card.
	 */
	public void clearSelection() {
		selected.clear();
	}
	
	// REVER
	/**
	 * Obtains the index of a given card in the hand.
	 * 
	 * @param card Card to search for
	 * @return The index of the card in the hand
	 * 
	 * @throws IllegalStateException if the card does not exist in the hand.
	 */
	public int indexOf(ICard card) {
		for(int i = 0; i < cards.size(); i++)
			if(cards.get(i).equals(card))
				return i;
		
		// TODO: rever -1
		throw new IllegalStateException("Card does not exist in the hand");
	}
	
	
	
	/**
	 * Removes a card.
	 * 
	 * @param index Index of the card
	 */
	public void remove(int index) {
		checkIndex(index);
		
		selected.remove(index);
		flipped.remove(index);
		
		for(int s : selected) {
			if(s > index) {
				selected.remove(s);
				selected.add(s - 1);
			}
		}
		
		for(int f : flipped) {
			if(f > index) {
				flipped.remove(f);
				flipped.add(f - 1);
			}
		}
		
		cards.remove(index);
		cardsText.remove(index);
	}
	
	
	// TODO: remove(Card)
	
	// TODO: get all cards
	
	/**
	 * Removes all cards.
	 */
	public void removeAll() {
		cards.clear();		
		selected.clear();
		flipped.clear();
		cardsText.clear();
	}
	
	private static ICard randomCard() {		
		return new ICard() {
			@Override
			public Rank getRank() {
				return Rank.random();
			}
			
			public Suit getSuit() {
				return Suit.random();
			}
		};
	}
	
}
