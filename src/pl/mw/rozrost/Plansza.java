package pl.mw.rozrost;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.swing.JPanel;

public class Plansza extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private int pixelSize = Core.Config.pixelSize;
	private int width = 600;
	private int height = 500;
	private int tabSizeX = this.width / this.pixelSize;
	private int tabSizeY = this.height / this.pixelSize;
	private P[][] P = new P[this.tabSizeX][this.tabSizeY];
	private double p_before = 0.0;
	private int rekAbleR = 10; // minimalna odleglosc 2ch zrekrystalizowanych zarodkow
	
	private int itr = 0; // aktualny krok iteracji

	/*
	 * inicjalizacja macierzy oraz planszy
	 */
	public Plansza() {

		for (int i = 0; i < this.tabSizeX; i++) {
			for (int j = 0; j < this.tabSizeY; j++) {
				this.P[i][j] = new P();
			}
		}

		// this.setBackground(SystemColor.text);
		// setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setPreferredSize(new Dimension(500, 500));

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int x = arg0.getX();
				int y = arg0.getY();
				Plansza.this.clickPixel(x, y);
				Plansza.this.refresh();
			}
		});

	}
    
	/*
	 * uruchomienie planszy w nowym watku, inicjalizacja iterakcji
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		System.out.println("Odpalam Plansza! ");
		while (true) {
			this.resizeP(); // rozmiar planszy
			if (Core.Config.StatusStart == 1) {
				this.reverse(); // P: prev = act
				this.refresh(); // iterakcja programu
				// System.out.print(".");
			} else { itr = 0; }
			try {
				Thread.sleep(Core.Config.delay);
			} catch (Exception ex) { }
		}
	}

	/*
	 * iterakcja programu
	 */
	public void refresh() {
		int x = 0; // ile pixeli zostalo do zapelnienia planszy ziarnami
		// ===== iteracja =========
		switch (Core.Config.metoda) {
		case 0:
			x = this.iteracja_naiwny();
			break;
		case 1: 
//			this.iteracja_rekrystqalizacja();
//			nie zaimplementowane :p
			break;
		}
		
		if (x==0 && Core.Config.rekrystalizacja==1) { // mozna rekrystalizowac
			++itr;
			this.rekrystqalizacja(itr);
		}
		// ===== rysuj =========
		repaint();
	}//--
	
	/*
	 * iterakcja rekrystalizacji - rozdziel gestosc dyslokacji + rozrost zrekrystalizowanych ziaren
	 */
	private void rekrystqalizacja(int itr) {
		double p = this.gestosc_dyslokacji(itr) - p_before;  // roznica dyslokacji
		//p_before += p;
		double psr = p/(this.tabSizeX*this.tabSizeY);  // dyslokacje na komorke
		double p_krytyczne = 4215840142323.42/(this.tabSizeX*this.tabSizeY);
		double p_tmp,p_all=0.0;
		P tmp;
		
		/* rozmiesz gestosc dyslokacji */
		
		for (int i = 0; i <= this.tabSizeX - 1; i++) {
			for (int j = 0; j <= this.tabSizeY - 1; j++) {
				//if (p_all>=p) continue;
				
				if (this.naGranicy(i, j)) {
					p_tmp = this.pij(70, 180, psr);
				} else {
					p_tmp = this.pij(0, 30, psr);
				}
				p_all += p_tmp;
				
				this.P[i][j].p += p_tmp;
				
				if (this.P[i][j].rek==1) continue; // ziarno zrekrystalizowane
				
				if (this.P[i][j].p > p_krytyczne && this.rekAble(i, j)) { // nowe ziarno [nz]
					this.P[i][j].set_rek(1);
					//System.out.println("Nowe ziarno w P["+i+"]["+j+"]");
				}//nz
			}//j
		}//i
		p_before += p_all; // jesli przydzielono za malo dyslokacji, dodaj brakujace do kolejnego kroku
		//System.out.println("psr = "+psr+"; p_all="+p_all+" ("+p+"); p_krytyczne = "+p_krytyczne);
		
		/* rozrost istniejacych zrekrystalizowanych ziaren  */
		for (int i = 0; i <= this.tabSizeX - 1; i++) {
			for (int j = 0; j <= this.tabSizeY - 1; j++) {
				if (this.P[i][j].act==0 || this.P[i][j].rek==1) continue; // brak ziarna lub komorka zrekrystalizowana

				tmp = this.makeAct(i, j, "rek"); // sasiad od ktorego mam  przejac kolor
				
				if (tmp.act < 0 ) 	continue; // brak sasiedztwa

				this.P[i][j].rek = tmp.rek;
				this.P[i][j].recolor_rek(tmp);
				
			}//f[j
		}//f[i
	}//---------------------------
	
	/*
	 * rekrystalizacja w trakcie rozrostu ziaren - brak implementacji!
	 */
	private void iteracja_rekrystqalizacja() {
		double p = this.gestosc_dyslokacji(this.itr);
		double psr = p/(double)this.tabSizeX*this.tabSizeY;

		for (int i = 0; i <= this.tabSizeX - 1; i++) {
			for (int j = 0; j <= this.tabSizeY - 1; j++) {
				if (this.P[i][j].prev==0) {
					this.P[i][j].p += this.pij(70, 120, psr);
				} else if (this.P[i][j].prev==1) {
					/*if (ziarno) {
						
					} else {
						this.P[i][j].p += this.pij(0, 30, psr);
					}*/
				} else { /* no to problem... */ }
				
			}//f[j
		}//f[i
	}//---------------------------
	
	/*
	 * wzor na gestosc dyslokacji
	 */
	private Double gestosc_dyslokacji(int itr) {
		if (itr<0) return 0.0;
		Double r;
		
		double A = 86710969050178.5;
		double B = 9.41268203527779;
		double t = (double)itr * (double)(Core.Config.delay)/1000.0;
		
		r = A/B+(1-A/B)*Math.exp(-B*t);
		return r;
	}
	
	/*
	 * losowanie wartosci sredniej gestosci dyslokacji w podanym zakresie procentowym
	 */
	private double pij(int l1, int l2, double psr) {
		Random r = new Random();
		return (double)((r.nextInt(l2-l1)+l1)/100.0)*psr;
	}
	
	/*rozrost naiwny*/
	
	/*
	 * losowanie gestosci dysloacji i rozrost zrekrystalizowanych ziaren
	 */
	private int iteracja_naiwny() {
		if (Core.Config.StatusStart == 0) return -1;
		int x = 0;
		P tmp;
		
			for (int i = 0; i <= this.tabSizeX - 1; i++) {
				for (int j = 0; j <= this.tabSizeY - 1; j++) {
					if (this.P[i][j].act == 1)
						continue;
					++x;
					tmp = this.makeAct(i, j, "act"); // wzroz sasiada od ktorego mam  przejac kolor
					if (tmp.act < 0 && tmp.prev < 0)
						continue;
					this.P[i][j].act = tmp.act;
					this.P[i][j].recolor(tmp);
					
				}//j
			}//i
		return x;
	}//--

	/*
	 * losuj zarodek w macierzy
	 */
	public void randomPixel() {
		Random r = new Random();
		int x = r.nextInt(this.width);
		int y = r.nextInt(this.height);
		this.clickPixel(x, y);
	}

	/*
	 * wyczysc macierz
	 */
	public void clean() {
		this.itr = 0; // wyzeruj licznik iterakcji
		
		for (int i = 0; i < this.tabSizeX; i++) {
			for (int j = 0; j < this.tabSizeY; j++) {
				this.P[i][j].set(0);
			}
		}
	}

	/*
	 * rozmieszczenie zarodkow na macierzy
	 */
	public void random() {
		Random r = new Random();
		int x, y;
		int srodek_x = this.tabSizeX / 2;
		int srodek_y = this.tabSizeY / 2;

		switch (Core.Config.rozmieszczenie) {
		case 1: // losowe rownomierne
			double px,py;
			
			py = Math.sqrt(this.tabSizeY*Core.Config.punkty/this.tabSizeX);
			px = ((double)Core.Config.punkty)/py;
			int x_size = (int)px;
			int y_size = (int)py;
			int x_width = this.tabSizeX/x_size;
			int y_height = this.tabSizeY/y_size;
			srodek_x = x_size / 2;
			srodek_y = y_size / 2;

			//System.out.println("x_size= "+x_size+"; y_size = "+y_size);
			for (int i=0;i<x_size;i++) {
				for(int j=0;j<y_size;j++) {
					this.P[(i*2+1)*x_width/2][(j*2+1)*y_height/2].set_random(1);
				}
			}
			
			break;// 1
		case 2: // losowe promien okregu
			int PRx,PRy;

			for (int i = 0; i < 360; i = i + (360 / Core.Config.punkty)) {
				PRx = r.nextInt(this.tabSizeX / 2);
				PRy = r.nextInt(this.tabSizeY / 2);

				x = (int) (PRx * Math.cos(i * Math.PI / 180));
				x = x + srodek_x;
				y = (int) (PRy * Math.sin(i * Math.PI / 180));
				y = y + srodek_y;

				while (x < 0)
					x++;
				while (y < 0)
					y++;
				while (x >= this.tabSizeX)
					x--;
				while (y >= this.tabSizeY)
					y--;

				this.P[x][y].set_random(1);
			}// for
			break; // 2

		case 3: // losowe przypadkowe
			for (int i=0;i<Core.Config.punkty;i++) {
				x = r.nextInt(this.tabSizeX);
				y = r.nextInt(this.tabSizeY);
				this.P[x][y].set_random(1);
			}
			break;// 3

		case 4: // losowe z promieniem
			// Core.Config.rozmieszczenie_r
			List<Pkt> wsio = new ArrayList<Pkt>();
			for (int i = 0; i < this.tabSizeX; i++) 
				for (int j = 0; j < this.tabSizeY; j++) 
					wsio.add(new Pkt(i,j));
			
			for (int i=0;i<Core.Config.punkty;i++) {//i
				if (wsio.size()<1) { System.out.println("wyczerpano zasob punktow!"); return; }
				Pkt p = wsio.get(r.nextInt(wsio.size()));

				this.P[p.x][p.y].set_random(1);
				
				Iterator<Pkt> wsio_i = wsio.iterator();
				while(wsio_i.hasNext()) {//next
					Pkt v = wsio_i.next();
					if (p.inR(v, Core.Config.rozmieszczenie_r)) 
						wsio_i.remove();
				}//next
			}//i
			break;// 4
		}
	}// --

	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);

		for (int x = 0; x < this.tabSizeX; x++) {
			for (int y = 0; y < this.tabSizeY; y++) {

				if (this.P[x][y].RR>0 && this.P[x][y].GG>0 && this.P[x][y].BB>0) 
					g.setColor(new Color(this.P[x][y].RR, this.P[x][y].GG,
							this.P[x][y].BB));
				else
				g.setColor(new Color(this.P[x][y].R, this.P[x][y].G,
						this.P[x][y].B));

				g.fillRect(x * this.pixelSize, // position X
						y * this.pixelSize, // position Y
						this.pixelSize, // width
						this.pixelSize // height
				);
			}
		}
	}// g

	/*
	 * wczytaj zapisana macierz punktow
	 */
	public void loadMap() {
		// TODO Auto-generated method stub
		for (Entry<Pkt, P> o : Core.pkts.entrySet()) {
			this.P[o.getKey().x][o.getKey().y] = new P(o.getValue());
		}
	}

	/*
	 * exportuj tablice punktow
	 */
	public void saveMap() {
		Core.pkts.clear();
		for (int i = 0; i < tabSizeX; i++) {
			for (int j = 0; j < tabSizeY; j++) {
				if (this.P[i][j].act == 1) {
					Core.pkts.put(new Pkt(i, j), new P(this.P[i][j]));
				}
			}
		}
	}

	/*
	 * sprawdzaj czy rozmiar panszy (JPanel) sie zmienil i uaktualij rozmiar macierzy
	 */
	private void resizeP() {

		int w = this.width;
		int h = this.height;

		if (this.getHeight() > 0)
			h = this.getHeight();
		if (this.getWidth() > 0)
			w = this.getWidth();

		this.tabSizeX = this.width / this.pixelSize;
		this.tabSizeY = this.height / this.pixelSize;

		if (h != this.height || w != this.width) { // zmiana rozmiary planszy -  przebuduj tab [t]
			
		
		System.out.print("Zmieniam rozmiar planszy z: " + this.tabSizeX
				+ " x " + this.tabSizeY + ";"); // --------

		P[][] P_new = new P[w / this.pixelSize][h / this.pixelSize];
		for (int i = 0; i < w / this.pixelSize; i++) {
			for (int j = 0; j < h / this.pixelSize; j++) {
				P_new[i][j] = new P();
			}
		}
		// foreach old array
		for (int i = 0; i < this.tabSizeX; i++) {
			for (int j = 0; j < this.tabSizeY; j++) {
				if (i >= (w / this.pixelSize) || j >= (h / this.pixelSize))
					continue;
				P_new[i][j] = this.P[i][j];
			}
		}

		// set new size
		this.width = w;
		this.height = h;
		this.tabSizeX = this.width / this.pixelSize;
		this.tabSizeY = this.height / this.pixelSize;

		System.out.print("na: " + this.tabSizeX + " x " + this.tabSizeY
				+ "; \n"); // -------------------------
		// odswiez tab
		this.P = P_new;
		}//[t]
	}

	/*
	 * sprawdz czy dana komorka moze zrekrystalizowac
	 * - czy w jej otoczeniu (this->rekAbleR) jest juz zrekrystalizowany zarodek
	 */
	private boolean rekAble(int x, int y) {
		for (int i=1;i<this.rekAbleR;i++) {
			for (int j=1;j<this.rekAbleR;j++) {
				if (y-j>=0 && x-i>=0) if (this.P[x-i][y-j].rek==1) return false;
				if (y+j<this.tabSizeY && x+i<this.tabSizeX) if (this.P[x+i][y+j].rek==1) return false;
			}//j
		}//i
		return true;
	}//--
	
	/*
	 * znajdz sasiada danego punktu
	 * 
	 * @param t - ziarno lub zrekrystalizowane ziarno
	 * @return P - sasiad danego punktu
	 */
	private P makeAct(int x, int y, String t) {
		int[] s = { 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] s_pattern = { 0, 0, 0, 0, 0, 0, 0, 0 };
		Map<Integer, int[]> pattern = new HashMap<Integer, int[]>();

		pattern.put(0, new int[] { 1, 1, 1, 1, 1, 1, 1, 1 }); // Moor
		pattern.put(1, new int[] { 0, 1, 0, 1, 0, 1, 0, 1 }); // von Nauman
		pattern.put(2, new int[] { 1, 1, 0, 1, 1, 1, 0, 1 }); // Hexagonal left
		pattern.put(3, new int[] { 0, 1, 1, 1, 0, 1, 1, 1 }); // Hexagonal right
		pattern.put(4, new int[] {}); // Hexagonal random
		pattern.put(5, new int[] { 1, 1, 0, 0, 0, 1, 1, 1 }); // Pentagonal left
		pattern.put(6, new int[] { 0, 1, 1, 1, 1, 1, 0, 0 }); // Pentagonal
																// right
		pattern.put(7, new int[] { 1, 1, 1, 1, 0, 0, 0, 1 }); // Pentagonal top
		pattern.put(8, new int[] { 0, 0, 0, 1, 1, 1, 1, 1 }); // Pentagonal
																// bottom
		pattern.put(9, new int[] {}); // Pentagonal random

		/*
		 * 0 - lewa g�rna; 1 - centralna g�rna; 2 - prawa g�rna; 3 - prawa
		 * �rodkowa 4 - prawa dolna; 5 - centralna dolna; 6 - lewa dolna; 7 -
		 * lewa �rodkowa
		 */
		int left_x = x - 1;
		int right_x = x + 1;
		int top_y = y - 1;
		int bottom_y = y + 1;

		if (Core.Config.bc == 0) { // periodyczno��
			if (left_x < 0)
				left_x = this.tabSizeX - 1;
			if (right_x > this.tabSizeX - 1)
				right_x = 0;

			if (top_y < 0)
				top_y = this.tabSizeY - 1;
			else if (bottom_y > this.tabSizeY - 1)
				bottom_y = 0;
		}

		if (t=="act") {
		// uzupe�nij tabele sasiad�w
		if (left_x >= 0 && top_y >= 0)
			s[0] = this.P[left_x][top_y].prev;
		if (top_y >= 0)
			s[1] = this.P[x][top_y].prev;
		if (top_y >= 0 && right_x <= this.tabSizeX - 1)
			s[2] = this.P[right_x][top_y].prev;
		if (right_x <= this.tabSizeX - 1)
			s[3] = this.P[right_x][y].prev;
		if (bottom_y <= this.tabSizeY - 1 && right_x <= this.tabSizeX - 1)
			s[4] = this.P[right_x][bottom_y].prev;
		if (bottom_y <= this.tabSizeY - 1)
			s[5] = this.P[x][bottom_y].prev;
		if (left_x >= 0 && bottom_y <= this.tabSizeY - 1)
			s[6] = this.P[left_x][bottom_y].prev;
		if (left_x >= 0)
			s[7] = this.P[left_x][y].prev;
		} else if (t=="rek") {
			if (left_x >= 0 && top_y >= 0)
				s[0] = this.P[left_x][top_y].rek_prev;
			if (top_y >= 0)
				s[1] = this.P[x][top_y].rek_prev;
			if (top_y >= 0 && right_x <= this.tabSizeX - 1)
				s[2] = this.P[right_x][top_y].rek_prev;
			if (right_x <= this.tabSizeX - 1)
				s[3] = this.P[right_x][y].rek_prev;
			if (bottom_y <= this.tabSizeY - 1 && right_x <= this.tabSizeX - 1)
				s[4] = this.P[right_x][bottom_y].rek_prev;
			if (bottom_y <= this.tabSizeY - 1)
				s[5] = this.P[x][bottom_y].rek_prev;
			if (left_x >= 0 && bottom_y <= this.tabSizeY - 1)
				s[6] = this.P[left_x][bottom_y].rek_prev;
			if (left_x >= 0)
				s[7] = this.P[left_x][y].rek_prev;
		} else { System.out.println("-- blad: "+t+" nie ma sensu :[ --"); }
		
		P newP = new P();
		newP.set(-1);

		Random r = new Random();
		switch (Core.Config.sasiedztwo) {
		case 4:
			s_pattern = pattern.get(r.nextInt(2) + 2);
			break;
		case 9:
			s_pattern = pattern.get(r.nextInt(4) + 5);
			break;
		default:
			s_pattern = pattern.get(Core.Config.sasiedztwo);
			break;
		}

		if (s[0] == 1 && s_pattern[4] == 1)
			return this.P[left_x][top_y];
		else if (s[1] == 1 && s_pattern[5] == 1)
			return this.P[x][top_y];
		else if (s[2] == 1 && s_pattern[6] == 1)
			return this.P[right_x][top_y];
		else if (s[3] == 1 && s_pattern[7] == 1)
			return this.P[right_x][y];
		else if (s[4] == 1 && s_pattern[0] == 1)
			return this.P[right_x][bottom_y];
		else if (s[5] == 1 && s_pattern[1] == 1)
			return this.P[x][bottom_y];
		else if (s[6] == 1 && s_pattern[2] == 1)
			return this.P[left_x][bottom_y];
		else if (s[7] == 1 && s_pattern[3] == 1)
			return this.P[left_x][y];
		return newP;

	}

	/*
	 * zapis obecnego stanu komorek jako poprzedni krok czasowy
	 */
	private void reverse() {
		for (int i = 0; i < this.tabSizeX; i++) {
			for (int j = 0; j < this.tabSizeY; j++) {
				this.P[i][j].prev = this.P[i][j].act;
				this.P[i][j].rek_prev = this.P[i][j].rek;
			}
		}
	}

	/*
	 * generuj nowy zarodek przez klikniecie myszka
	 */
	private void clickPixel(int x, int y) {
		int pixel_x = x / this.pixelSize;
		int pixel_y = y / this.pixelSize;
		// int tmp_s = 0;

		while (pixel_x < 1)
			pixel_x++;
		while (pixel_x >= this.tabSizeX - 1)
			pixel_x--;
		while (pixel_y < 1)
			pixel_y++;
		while (pixel_y >= this.tabSizeY - 1)
			pixel_y--;

		if (pixel_x >= this.tabSizeX || pixel_y >= this.tabSizeY)
			return;

		if (this.P[pixel_x][pixel_y].act == 0) {
			this.P[pixel_x][pixel_y] = new P();
			this.P[pixel_x][pixel_y].set_random(1);
		}

		this.reverse();
	}

	/*
	 * czy dana komorka lezy na granicy swojego ziarna
	 */
	private boolean naGranicy(int x, int y) {
		boolean[] s = new boolean[8];

		int left_x = x - 1;
		int right_x = x + 1;
		int top_y = y - 1;
		int bottom_y = y + 1;

		if (Core.Config.bc == 0) { // periodycznosc
			if (left_x < 0)
				left_x = this.tabSizeX - 1;
			if (right_x > this.tabSizeX - 1)
				right_x = 0;

			if (top_y < 0)
				top_y = this.tabSizeY - 1;
			else if (bottom_y > this.tabSizeY - 1)
				bottom_y = 0;
		}

		if (left_x >= 0 && top_y >= 0)
			s[0] = this.P[left_x][top_y].colorMatch(this.P[x][y]);
		if (top_y >= 0)
			s[1] = this.P[x][top_y].colorMatch(this.P[x][y]);
		if (top_y >= 0 && right_x <= this.tabSizeX - 1)
			s[2] = this.P[right_x][top_y].colorMatch(this.P[x][y]);
		if (right_x <= this.tabSizeX - 1)
			s[3] = this.P[right_x][y].colorMatch(this.P[x][y]);
		if (bottom_y <= this.tabSizeY - 1 && right_x <= this.tabSizeX - 1)
			s[4] = this.P[right_x][bottom_y].colorMatch(this.P[x][y]);
		if (bottom_y <= this.tabSizeY - 1)
			s[5] = this.P[x][bottom_y].colorMatch(this.P[x][y]);
		if (left_x >= 0 && bottom_y <= this.tabSizeY - 1)
			s[6] = this.P[left_x][bottom_y].colorMatch(this.P[x][y]);
		if (left_x >= 0)
			s[7] = this.P[left_x][y].colorMatch(this.P[x][y]);
		
		//System.out.println("rgb("+this.P[left_x][top_y].R+") == rgb("+this.P[x][y].R+")");
		for(boolean ss : s) {
			if (!ss) return true;
		}
		return false;

	}
	
}//$
