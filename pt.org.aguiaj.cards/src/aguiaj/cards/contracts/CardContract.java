package aguiaj.cards.contracts;

import aguiaj.cards.ICard;
import aguiaj.cards.Rank;
import aguiaj.cards.Suit;
import pt.org.aguiaj.extensibility.contracts.AbstractContractDecoractor;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;

public class CardContract extends AbstractContractDecoractor<ICard> implements ICard {

	public CardContract(ICard card) {
		super(card);
	}

	@Override
	public Rank getRank() {
		Rank r = getWrappedObject().getRank();
		if(r == null)
			throw new PostConditionException(getWrappedObject().getClass(), "getRank", "card rank cannot be null");
		
		return r;
	}

	@Override
	public Suit getSuit() {
		Suit s = getWrappedObject().getSuit();
		if(s == null)
			throw new PostConditionException(getWrappedObject().getClass(), "getSuit", "card suit cannot be null");
		
		return s;
	}

}
