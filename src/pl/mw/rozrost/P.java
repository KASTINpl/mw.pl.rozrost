package pl.mw.rozrost;

import java.util.Random;

/*
 * komorka macierzy; rozmiar = Core.Config.pixelSize
 */
public class P {
	// komorka jest ziarnem lub nie
	public int act = 0;
	public int prev = 0;

	// stan zrekrystalizowania komorki
	public int rek = 0;
	public int rek_prev = 0;

	// gestosc dyslokacji
	public double p = 0.0;

	// RGB niezrekrystalizowanych ziaren
	public int R = 250;
	public int G = 250;
	public int B = 250;
	
	// RGB zrekrystalizowanych ziaren
	public int RR = -1;
	public int GG = -1;
	public int BB = -1;

	public P() {
	}

	public P(P p) {
		this.prev = p.prev;
		this.act = p.act;
		this.rek = p.rek;
		this.rek_prev = p.rek_prev;
		this.p = p.p;
		this.R = p.R;
		this.G = p.G;
		this.B = p.B;
		this.RR = p.RR;
		this.GG = p.GG;
		this.BB = p.BB;
	}

	public void set_rek(int x) {
		this.rek = x;
		this.rek_prev = x;

		this.p = 0.0;

		Random r = new Random();

		this.RR = r.nextInt(250);
		this.GG = r.nextInt(250);
		this.BB = r.nextInt(250);
	}

	public void set(int x) {
		this.prev = x;
		this.act = x;
		this.rek = 0;
		this.rek_prev = 0;

		this.p = 0.0;

		this.R = 250;
		this.G = 250;
		this.B = 250;
		this.RR = -1;
		this.GG = -1;
		this.BB = -1;
	}

	public void set_random(int x) {
		this.set(x);

		Random r = new Random();

		this.R = r.nextInt(250);
		this.G = r.nextInt(250);
		this.B = r.nextInt(250);
	}

	public void recolor_rek(P p) {
		this.RR = p.RR;
		this.GG = p.GG;
		this.BB = p.BB;
	}

	public void recolor(P p) {
		this.R = p.R;
		this.G = p.G;
		this.B = p.B;
	}

	public boolean colorMatch(P p) {
		if (this.R==250 && this.G==250 && this.B==250) return false;
		if (this.R == p.R && this.G == p.G && this.B == p.B)
			return true;
		return false;
	}
	public Integer color2int() {
		int rgb = this.R;
		rgb = (rgb << 8) + this.G;
		rgb = (rgb << 8) + this.B;
		return rgb;
	}
	public void loadRGB(Integer _rgb) {
		int rgb = _rgb;
		this.R = (rgb >> 16) & 0xFF;
		this.G = (rgb >> 8) & 0xFF;
		this.B = rgb & 0xFF;
	}
	
	public boolean equals(P p) {
	    return this.colorMatch(p);
	}
}
