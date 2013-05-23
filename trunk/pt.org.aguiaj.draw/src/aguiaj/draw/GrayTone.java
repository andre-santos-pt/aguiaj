package aguiaj.draw;

import aguiaj.draw.Color;


public class GrayTone implements Color {

	private int value;
	
	private static final int MAX = 255;
//	public static final GrayTone WHITE = new GrayTone(MAX);
//	public static final GrayTone BLACK = new GrayTone(0);
//	public static final GrayTone GRAY = new GrayTone(MAX/2);
	
	public GrayTone(int value) {
		this.value = value;
	}
	
	public static GrayTone random() {
		return new GrayTone((int) (Math.random() * (MAX+1)));
	}
	
	@Override
	public int getR() {
		return value;
	}

	@Override
	public int getG() {
		return value;
	}

	@Override
	public int getB() {
		return value;
	}
	
	public void set(int v) {
		value = v;
	}
	
	public GrayTone invert() {
		return new GrayTone(MAX-value);
	}

}
