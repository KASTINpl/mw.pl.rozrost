package pl.mw.rozrost;

import java.util.Random;

public class P {
	public int prev=0;
	public int act=0;
	
	public int rek=0;
	public int rek_prev=0;
	
	public double p;
	
	public int R=250;
	public int G=250;
	public int B=250;
	
	public void set_rek(int x) {
		this.rek=x;
		this.rek_prev=x;
		
		this.p = 0.0;
		
		Random r = new Random(); 
		
		this.R = r.nextInt(250);
		this.G = r.nextInt(250);
		this.B = r.nextInt(250);
	}
	
	public void set(int x){
		this.prev=x;
		this.act=x;
		this.rek=0;
		this.rek_prev=0;
		
		this.p = 0.0;
		
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
		r.p = this.p;
		return r;
	}
	
	public void set_random(int x){
		this.set(x);
		
		Random r = new Random(); 
		
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
