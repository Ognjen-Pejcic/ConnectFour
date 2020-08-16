package clienthandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import igra.Igra;
import igra.Soba;
import server.Main;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socketZaKomunikaciju = null;
	String username;
	boolean uGlobalChatu = false;
	public boolean uSobi = false;
	int brojigraca;
	public Igra igra = new Igra();
	public int nesto = 1;

	public ClientHandler(Socket socketZaKomunikaciju) throws IOException {
		this.socketZaKomunikaciju = socketZaKomunikaciju;

		if (Main.onlineKorisnici.isEmpty())
			brojigraca = 1;
		else
			brojigraca = 2;
		clientInput = new BufferedReader(new InputStreamReader(socketZaKomunikaciju.getInputStream()));
		clientOutput = new PrintStream(socketZaKomunikaciju.getOutputStream());
	}

	public void ispisMatrice(Igra ii) {

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

			clientInput = new BufferedReader(new InputStreamReader(socketZaKomunikaciju.getInputStream()));
			clientOutput = new PrintStream(socketZaKomunikaciju.getOutputStream());

			ispisPorukeOdServera("Unesite korisnicko ime:");
			username = clientInput.readLine();
			clientOutput.println(">>> Dobrodosao/la " + username);

			int izbor = 0;

			do {
				ispisMenija();
				while (true) {
					try {
						izbor = Integer.parseInt(clientInput.readLine());
					} catch (NumberFormatException e) {
						izbor = 0;
					}
					if (izbor >= 1 && izbor <= 6)
						break;
					else
						ispisPorukeOdServera("Pogresan unos, unesite broj izmedju 1 i 6");
				}
				switch (izbor) {

				case 1:// globalni chat
					this.uGlobalChatu = true;
					ispisPorukeOdServera("Za izlaz unesite \"izlaz\"");
					slanjePorukeOstalimUcesnicima(username + " je usao u chat sobu!");
					while (true) {
						String message = clientInput.readLine();
						if (message.toLowerCase().equals("izlaz"))
							break;
						slanjePorukeSvimUcesnicima(username + ": " + message);
					}
					slanjePorukeSvimUcesnicima(username + " je napustio chat!");
					this.uGlobalChatu = false;
					break;

				case 2:// privatni chat

				case 3:// igra
					uSobi=true;
					Soba ova=null;
					if (Main.sobe.isEmpty()) {
						Main.sobe.add(new Soba(this));
						 ova =  Main.sobe.getFirst();
						ispisPorukeOdServera("Ceka se drugi igrac...");
					} else {
						for (Soba soba : Main.sobe) {
							if (soba.drugi == null) {
								soba.prikljucivanjeIgri(this);
								ova=soba;
								soba.prvi.ispisPorukeOdServera("Igrate protiv: " + username);
								ispisPorukeOdServera("Igrate protiv: " + soba.prvi.username);
							} else {
								Main.sobe.add(new Soba(this));
								ispisPorukeOdServera("Ceka se drugi igrac...");
								ova=soba;
							}
						}
					}
					//uSobi = true;
					while(ova.drugi==null)continue;
				/*	while (this.uSobi==true) {
						continue;
					}
					break;*/
				case 4:// Dodaj prijatelja
				case 5:// Zahtevi za prijateljstvo
				case 6:// logout

				default:

				}
				
			} while (izbor != 6);
			clientOutput.println("Kraj");
			Main.onlineKorisnici.remove(this);
			socketZaKomunikaciju.close();

			
			

			
		} catch (IOException e) {

			Main.onlineKorisnici.remove(this);

			for (ClientHandler client : Main.onlineKorisnici) {

				if (client != this) {
					client.clientOutput.println(">>> Korisnik " + username + " je napustio/la u chat!!!");
				}
			}
		} //catch (InterruptedException e) {}

	}

	public void ispisPorukeOdServera(String poruka) {
		clientOutput.println(poruka);
	}

	private void slanjePorukeSvimUcesnicima(String poruka) {
		for (ClientHandler klijent : Main.onlineKorisnici) {
			if (klijent.uGlobalChatu == true)
				klijent.clientOutput.println(poruka);
		}
	}

	private void slanjePorukeOstalimUcesnicima(String poruka) {
		for (ClientHandler klijent : Main.onlineKorisnici) {
			if (klijent != this) {
				if (klijent.uGlobalChatu == true)
					klijent.clientOutput.println(poruka);
			}
		}
	}

	private void ispisMenija() {

		clientOutput.println(
				"1. Globalni chat\n2. Privatni chat\n3. Igraj\n4. Dodaj prijatelja\n5. Zahtevi za prijateljstvo\n6. Logout\nVas izbor:");

	}

	public String unos() throws IOException {
		return clientInput.readLine();
	}

	
}
