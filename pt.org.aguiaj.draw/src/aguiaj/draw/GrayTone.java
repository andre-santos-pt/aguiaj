package aguiaj.draw;

import aguiaj.draw.Color;


public class GrayTone implements Color {

	
	private static final int MAX = 255;
	private static final GrayTone[] tones;
	static {
		tones = new GrayTone[MAX+1];
		for(int i = 0; i <= MAX; i++)
			tones[i] = new GrayTone(i);
	}
	

	private int value;
	
//	public static final GrayTone WHITE = new GrayTone(MAX);
//	public static final GrayTone BLACK = new GrayTone(0);
//	public static final GrayTone GRAY = new GrayTone(MAX/2);
	
	private GrayTone(int value) {
		this.value = value;
	}
	
	public static GrayTone get(int v) {
		return tones[v];
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
