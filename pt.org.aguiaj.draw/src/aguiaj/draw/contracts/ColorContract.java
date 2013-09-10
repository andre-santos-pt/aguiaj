package aguiaj.draw.contracts;

import aguiaj.draw.RGBColor;
import pt.org.aguiaj.extensibility.contracts.AbstractContractDecoractor;
import pt.org.aguiaj.extensibility.contracts.InvariantException;
import pt.org.aguiaj.extensibility.contracts.PostConditionException;

public class ColorContract extends AbstractContractDecoractor<RGBColor> implements RGBColor {

	public ColorContract(RGBColor instance) {
		super(instance);
	}

	@Override
	public int getR() {
		int r = instance.getR();
		validate(r);
		return r;
	}

	@Override
	public int getG() {
		int g = instance.getG();
		validate(g);
		return g;
	}

	@Override
	public int getB() {
		int b = instance.getB();
		validate(b);
		return b;
	}

	@Override
	public void checkInvariant() throws InvariantException {
		validate(instance);
	}
	
	public static void validate(RGBColor color) {
		validate(color.getR(), "R");
		validate(color.getG(), "G");
		validate(color.getB(), "B");
	}
	
	private static void validate(int v, String comp) {
		if(v < 0 || v >= 256)
			throw new PostConditionException(v + " : invalid " + comp + " value, must be within [0, 255]");
	}
	
}
