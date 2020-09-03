package igra;

import java.util.Random;

import clienthandler.ClientHandler;

public class Soba {

	public ClientHandler prvi;
	public ClientHandler drugi;
	public Igra igra;
	public int kod;
	public Soba(ClientHandler igrac) {
		this.prvi=igrac;
		kod = new Random().nextInt();
	}
	
	public  void prikljucivanjeIgri(ClientHandler igrac) {
		this.drugi = igrac;
		igra= new Igra(prvi, drugi, this);
		igra.start();
	}
	public void zapocniIgru() {
		igra.start();
	}
	public void prikljucivanjeIgri(ClientHandler igrac,int kod) {
		this.drugi = igrac;
		igra= new Igra(prvi, drugi, this);
		igra.start();
	}
}
