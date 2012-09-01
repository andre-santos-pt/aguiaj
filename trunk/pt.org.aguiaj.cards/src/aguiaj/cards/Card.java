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
package aguiaj.cards;

import java.util.Arrays;
import java.util.Random;

/**
 * Represents the playing cards. Each card has a unique instance.
 * 
 * @author Andre L. Santos
 */
public class Card {
	/**
	 * Card rank.
	 */
	public final Rank rank;
	
	/**
	 * Card suit.
	 */
	public final Suit suit;
	
		
	private static final Card[] all;
	private static final Suit[] suits;
	private static final Rank[] ranks;
	
	static {
	    suits = Suit.values();	    
	    ranks = Rank.values();   
	    all = new Card[suits.length * ranks.length];
	    for(int i = 0; i < suits.length; i++)
	    	for(int j = 0; j < ranks.length; j++)
	    		all[i*ranks.length + j] = new Card(ranks[j], suits[i]);
	}

	protected Card(Rank rank, Suit suit) {		
		if(rank == null)
			throw new NullPointerException("Rank cannot be null");
		
		if(suit == null)
			throw new NullPointerException("Suit cannot be null");
		
		this.rank = rank;
		this.suit = suit;		
	}
	
	/**
	 * Obtain a card given a rank and a suit.
	 * 
	 * @param rank Rank
	 * @param suit Suit
	 * 
	 * @return The card with the given <code>rank</code> and <code>suit</code>
	 */
	public static Card valueOf(Rank rank, Suit suit) {
		if(rank == null || suit == null)
			throw new NullPointerException("Arguments cannot be null");
		
		return all[suit.ordinal()*ranks.length + rank.ordinal()];
	}
	
	/**
	 * Obtains a random card.
	 * @return One out of the 52 cards.
	 */
	public static Card random() {		
		return all[new Random().nextInt(all.length)];
	}
	
	/**
	 * Creates an array with random cards.
	 *  
	 * @param length Array length
	 * @return An array with the given length, possibly with duplicate cards.
	 */
	public static Card[] randomArray(int length) {
		Card[] random = new Card[length];
		for(int i = 0; i < length; i++)
			random[i] = random();
		return random;
	}
	
	/**
	 * Creates an array with all the cards.
	 * 
	 * @return An array with length 52.
	 */
	public static Card[] allCards() {
		return Arrays.copyOf(all, all.length);
	}
	
	
	/**
	 * A textual representation of the card. 
	 * This textual representation can be used to obtain the card using valueOf(String).
	 */
	@Override
	public String toString() {
		return rank.name() + " " + suit.name();
	}	
	
	@Override
	public boolean equals(Object object) {
		return super.equals(object);
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + rank.ordinal();		
		result = 31 * result + suit.ordinal();
		return result;
	}
}
