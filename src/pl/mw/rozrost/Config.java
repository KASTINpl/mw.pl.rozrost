package pl.mw.rozrost;

/*
 * ustawienia - klasa statyczna Core.Config
 * 
 * @see pl.mw.rozrost.Core
 */
public class Config {
	public int StatusStart = 0;
	public int bc = 0;
	public int delay = 1;

	public int sasiedztwo = 0;
	public int punkty = 8;
	public int rozmieszczenie = 2;
	public int rozmieszczenie_r = 10; // minimalna przestrzen pomiedzy zarodkami
	
	public int pixelSize = 5;
	
	public int metoda = 0; // rozrost naiwny
	
	public int rekrystalizacja = 0; // po rozroscie ziaren
	
	public int mc = 0; // Monte Carlo ;)
	
	public boolean menuSaveMap = false;
	public boolean menuClean = true;
}
