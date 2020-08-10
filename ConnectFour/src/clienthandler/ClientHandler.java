package clienthandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import igra.Igra;
import odigravanje.Main;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socketZaKomunikaciju = null;
	String username;
	int brojigraca;
	public Igra igra = new Igra();

	public ClientHandler(Socket socketZaKomunikaciju) throws IOException {
		this.socketZaKomunikaciju = socketZaKomunikaciju;
		
		if (Main.onlineKorisnici.isEmpty())
			brojigraca = 1;
		else
			brojigraca = 2;
		clientInput = new BufferedReader(new InputStreamReader(socketZaKomunikaciju.getInputStream()));
		clientOutput = new PrintStream(socketZaKomunikaciju.getOutputStream());
	}

	public void ispisi(Igra ii) {

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				clientOutput.print(ii.matrica[i][j] + "\t");
			}
			clientOutput.println();

		}

	}

	@Override
	public synchronized void run() {
		
		try {
			/*synchronized (this) {
				if(brojigraca==1)this.wait();
				if(brojigraca==2)this.notifyAll();
			}*/
			
			clientInput = new BufferedReader(new InputStreamReader(socketZaKomunikaciju.getInputStream()));
			clientOutput = new PrintStream(socketZaKomunikaciju.getOutputStream());

			clientOutput.println("Unesite korisnicko ime:");
			username = clientInput.readLine();
			clientOutput.println(">>> Dobrodosao/la " + username);

			for (ClientHandler klijent : Main.onlineKorisnici) {
				if (klijent != this) {
					klijent.clientOutput.println(">>> Korisnik " + username + " je usao/la u chat sobu!");
				}
			}

			
			String noviunos=""; 
			while(!(noviunos.equals("quit"))){
				igra.napuniMatricuZaTest();
		boolean pobeda=false;
		 	do {
		 		
		 		/*  ClientHandler c2 = null; for (ClientHandler client : Main.onlineKorisnici) {
			 if(client!=this) c2=client; }*/
			  
	// synchronized(c2) {
			  if (brojigraca == 1) {
			  
				/*  for (Object client : Main.onlineKorisnici) {
			  if(client!=this)
			  client.wait();
			  }*/
			  
			  clientOutput.println("Unesi kolonu(prvi igrac)"); 
			  int prva1=-1;
			  boolean proveraUnosa=false;
			  do {
				  String unos = clientInput.readLine();
				  char[] kolona = unos.toCharArray();
				  if(kolona.length==1 && 
						  Character.isDigit(kolona[0])&&
						  (Integer.parseInt(unos)>=0&&Integer.parseInt(unos)<=igra.matrica.length-1)) {
				  proveraUnosa=true;
				  prva1 = Integer.parseInt(unos);
				  }
				  else 
					  clientOutput.println("Nepravilan unos, unesite samo cifru izmedju 0 i " + (igra.matrica.length-1)); 
			  }while(proveraUnosa==false);
			 // Character.isDigit(ch)
		
			 // clientOutput.println("Unesi drugu koordinatu(prvi igrac)"); int druga1 =
		//  Integer.parseInt(clientInput.readLine()); 
			  int provera=0;
			 do { 
				  provera=igra.postavi(prva1,brojigraca);
				 if(provera==1) {
					 clientOutput.println("Kolona je popunjena, ne mozete tu vise da ubacujete");
					 }else if(provera==2) {
						 clientOutput.println("Kraj igre-nereseno");
						 break;
					 }
				 
				 
				 }while(provera!=0);
			 if(provera==2)break;
			  ispisi(igra); 
			  for (ClientHandler client : Main.onlineKorisnici){ 
				  if (client != this) { 
					  client.igra.postavi(prva1, this.brojigraca);
		 client.ispisi(client.igra);} 
				  }
			  
			  
			// this.notifyAll();
			  
		  } if (brojigraca == 2) {
			  
		/*  for (Object client : Main.onlineKorisnici) {
			  if(client!=this)
			  client.wait(); }
			  */
			  int prva1=-1;
			  clientOutput.println("Unesi kolonu(drugi igrac)"); 
			  boolean proveraUnosa=false;
			  do {
				  if (igra.proveraPobede(1)==true) { 
					  pobeda = true;
				  clientOutput.println(" Pobedili ste"); 
				  for (ClientHandler client : Main.onlineKorisnici) { 
					  if (client != this) {
				  client.clientOutput.println(" prvi je pobedio"); } 
					  } 
				  if (pobeda) break; 
				  }
				if (igra.proveraPobede(2)==true) {
					pobeda = true;
				  clientOutput.println(" Pobedili ste"); 
				  for (ClientHandler client :Main.onlineKorisnici) { 
					  if (client != this) {
				  client.clientOutput.println(" Drugi je pobedio");
				  } 
					  } 
				  if (pobeda) break; 
				  }
				  String unos = clientInput.readLine();
				  char[] kolona = unos.toCharArray();
				  if(kolona.length==1 &&
						  Character.isDigit(kolona[0]) &&
						  (Integer.parseInt(unos)>=0&&Integer.parseInt(unos)<=igra.matrica.length-1)) {
				  proveraUnosa=true;
				  prva1 = Integer.parseInt(unos);
				  }
				  else 
					  clientOutput.println("Nepravilan unos, unesite samo cifru izmedju 0 i " + (igra.matrica.length-1)); 
			  }while(proveraUnosa==false);
			//  int prva1 = Integer.parseInt(clientInput.readLine());
		  //clientOutput.println("Unesi drugu koordinatu(drugi igrac)"); 
			  //int druga1 = Integer.parseInt(clientInput.readLine()); 
			  igra.postavi(prva1, brojigraca); 
			  ispisi(igra); for (ClientHandler client : Main.onlineKorisnici)
			  { if (client != this) { client.igra.postavi(prva1, this.brojigraca);
		  client.ispisi(client.igra);} }
			  
			 // this.notifyAll();
			  
			  } if (igra.proveraPobede(1)==true) { 
				  pobeda = true;
			  clientOutput.println(" Pobedili ste"); 
			  for (ClientHandler client : Main.onlineKorisnici) { 
				  if (client != this) {
			  client.clientOutput.println(" prvi je pobedio"); } 
				  } 
			  if (pobeda) break; 
			  }
			if (igra.proveraPobede(2)==true) {
				pobeda = true;
			  clientOutput.println(" Pobedili ste"); 
			  for (ClientHandler client :Main.onlineKorisnici) { 
				  if (client != this) {
			  client.clientOutput.println(" Drugi je pobedio");
			  } 
				  } 
			  if (pobeda) break; 
			  }
	 //}
			  } while (true);
			 clientOutput.println("Za izlaz unesite: quit, za novu partiju:  nova");
			 noviunos = clientInput.readLine();
		}
	/*		
			  String message = clientInput.readLine();
			  
			 while (true) { // ako poruka sadrzi niz karaktera koji ukazuju na izlaz,
			  //izlazi se iz petlje, // korisnik se izbacuje // iz liste online usera na
			// serveru i obavestavaju se ostali da je doticni // napustio chat
			 
			  if (message.startsWith("***quit")) { break; } } // regularna poruka se
			 // prosledjuje svima iz liste online usera
			  
			  for (ClientHandler klijent : Main.onlineKorisnici) {
			  klijent.clientOutput.println("[" + username + "]: " + message); }
			 
			  // korisniku koji napusta chat se salje pozdravna poruka
			
			clientOutput.println(">>> Dovidjenja " + username);

			// obavestavaju se ostali da je "user" napustio chat

			for (ClientHandler klijent : Main.onlineKorisnici) {
				if (klijent != this) {
					klijent.clientOutput.println(">>> Korisnik " + username + " je napustio chat!");
				}
			}

			// korisnik se izbacuje iz liste
*/
			

			// zatvaramo soket za komunikaciju

			
		 	/*
			for (ClientHandler klijent : Main.onlineKorisnici) {
				if (klijent != this) {
					Main.onlineKorisnici.remove(klijent);
					klijent.socketZaKomunikaciju.close();
				}
			}*/
			clientOutput.println("Kraj");
			Main.onlineKorisnici.remove(this);
			socketZaKomunikaciju.close();

		}catch(

	IOException e)
	{

		Main.onlineKorisnici.remove(this);

		for (ClientHandler client : Main.onlineKorisnici) {

			if (client != this) {
				client.clientOutput.println(">>> Korisnik " + username + " je napustio/la u chat!!!");
			}
		}
	}

}
	}
