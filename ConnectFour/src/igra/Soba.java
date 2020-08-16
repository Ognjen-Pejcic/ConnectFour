package igra;

import clienthandler.ClientHandler;

public class Soba {

	public ClientHandler prvi;
	public ClientHandler drugi;
	public Igra igra;
	
	public Soba(ClientHandler igrac) {
		this.prvi=igrac;
	}
	
	public  void prikljucivanjeIgri(ClientHandler igrac) {
		this.drugi = igrac;
		igra= new Igra(prvi, drugi, this);
		igra.start();
	}
}
