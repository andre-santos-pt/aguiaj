package aguiaj.draw.contracts;

import aguiaj.draw.RGBColor;
import pt.org.aguiaj.extensibility.contracts.AbstractContractDecoractor;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;

public class RGBColorContract extends AbstractContractDecoractor<RGBColor> implements RGBColor {

	public RGBColorContract(RGBColor instance) {
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
	
	private static void validate(RGBColor color) {
		validate(color.getR(), "R");
		validate(color.getG(), "G");
		validate(color.getB(), "B");
	}
	
	private static void validate(int v, String comp) {
		if(v < 0 || v >= 256)
			throw new PostConditionException(v + " : invalid " + comp + " value, must be within [0, 255]");
	}
	
}
