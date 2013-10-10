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

import java.util.Random;

/**
 * Represents the 13 card ranks.
 * 
 * @author Andre L. Santos
 */
public enum Rank {
	DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;
	
	/**
	 * Obtains a random rank.
	 * @return One out of the 13 ranks.
	 */
	public static Rank random() {
		Rank[] values = values();
		return values[new Random().nextInt(values.length)];
	}
}
