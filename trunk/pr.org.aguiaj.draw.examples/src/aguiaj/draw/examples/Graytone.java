package aguiaj.draw.examples;

import aguiaj.draw.RGBColor;


public class Graytone implements RGBColor {
	private static final int MAX = 255;
	
	public static final Graytone WHITE = new Graytone(MAX);
	public static final Graytone BLACK = new Graytone(0);
	public static final Graytone GRAY = new Graytone(MAX/2);
	
	private static final Graytone[] tones;
	
	static {
		tones = new Graytone[MAX+1];
		for(int i = 0; i <= MAX; i++)
			tones[i] = new Graytone(i);
	}
	

	private int value;
	
	private Graytone(int value) {
		this.value = value;
	}
	
	public static Graytone get(int v) {
		return tones[v];
	}
	
	public static Graytone random() {
		return new Graytone((int) (Math.random() * (MAX+1)));
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
	
	public Graytone invert() {
		return new Graytone(MAX-value);
	}

}
