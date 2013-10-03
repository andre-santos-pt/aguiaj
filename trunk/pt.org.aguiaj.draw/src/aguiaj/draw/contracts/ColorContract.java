package aguiaj.draw.contracts;

import pt.org.aguiaj.extensibility.contracts.AbstractContractDecoractor;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;
import aguiaj.draw.IColor;

public class ColorContract extends AbstractContractDecoractor<IColor> implements IColor {

	public ColorContract(IColor instance) {
		super(instance);
	}

	@Override
	public int getR() {
		int r = instance.getR();
		validate(r,"R");
		return r;
	}

	@Override
	public int getG() {
		int g = instance.getG();
		validate(g,"G");
		return g;
	}

	@Override
	public int getB() {
		int b = instance.getB();
		validate(b,"B");
		return b;
	}

	@Override
	public void checkInvariant() throws InvariantException {
		validate(instance);
	}
	
	private void validate(IColor color) {
		validate(color.getR(), "R");
		validate(color.getG(), "G");
		validate(color.getB(), "B");
	}
	
	private void validate(int v, String comp) {
		if(v < 0 || v >= 256)
			throw new PostConditionException(instance.getClass(), "get" + comp, v + " is an invalid " + comp + " value, must be within [0, 255]");
	}
	
}
