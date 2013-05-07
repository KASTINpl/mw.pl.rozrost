package pl.mw.rozrost;

import java.util.Random;

public class P {
	public int prev=0;
	public int act=0;
	
	public int R=250;
	public int G=250;
	public int B=250;
	
	public void set(int x){
		this.prev=x;
		this.act=x;
		
		this.R = 250;
		this.G = 250;
		this.B = 250;
	}
	
	public P clone() {
		P r = new P();
		r.prev = this.prev;
		r.act = this.act;
		r.R = this.R;
		r.G = this.G;
		r.B = this.B;
		return r;
	}
	
	public void set_random(int x){
		Random r = new Random(); 
		
		this.prev=x;
		this.act=x;
		this.R = r.nextInt(250);
		this.G = r.nextInt(250);
		this.B = r.nextInt(250);
	}

	public void recolor(P p) {
		this.R = p.R;
		this.G = p.G;
		this.B = p.B;
	}
}
