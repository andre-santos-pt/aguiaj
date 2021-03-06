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
package aguiaj.cards;

import java.util.Random;

/**
 * Represents the 4 card suits.
 * 
 * @author Andre L. Santos
 */
public enum Suit {
	CLUBS, DIAMONDS, HEARTS, SPADES;
		
	/**
	 * Obtains a random suit.
	 * @return One out of the 4 suits.
	 */
	public static Suit random() {
		Suit[] values = values();
		return values[new Random().nextInt(values.length)];
	}
}
