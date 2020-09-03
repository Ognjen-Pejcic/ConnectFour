package clienthandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import igra.Igra;
import igra.Soba;
import server.Main;

public class ClientHandler extends Thread {

	BufferedReader clientInput = null;
	PrintStream clientOutput = null;
	Socket socketZaKomunikaciju = null;
	String username;
	boolean uGlobalChatu = false;
	boolean uPrivateChatu = false;
	public boolean uSobi = false;
	int brojigraca;
	public Igra igra = new Igra();
	public int nesto = 1;
	public int idIgraca;
	
	LinkedList<ZahtevZaPrijateljstvo> zahtevi = new LinkedList<>();

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

			ispisiMeni();
			String izborS = clientInput.readLine();
			if (izborS.startsWith("1")) {
				registracija();

				logovanje();

			} else {
				logovanje();
			}

			clientOutput.println(">>> Dobrodosao/la " + username);
			this.getID();

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
					ispisiOnline();
			
					break;
				case 3:// igra
					uSobi = true;
					Soba ova = null;
					if (Main.sobe.isEmpty()) {
						Main.sobe.add(new Soba(this));
						ova = Main.sobe.getFirst();
						ispisPorukeOdServera("Ceka se drugi igrac...");
					} else {
						for (Soba soba : Main.sobe) {
							if (soba.drugi == null) {
								soba.prikljucivanjeIgri(this);
								ova = soba;
								soba.prvi.ispisPorukeOdServera("Igrate protiv: " + username);
								ispisPorukeOdServera("Igrate protiv: " + soba.prvi.username);
								// ova.zapocniIgru();
							} else {
								Main.sobe.add(new Soba(this));
								ispisPorukeOdServera("Ceka se drugi igrac...");
								ova = soba;
								// ova.zapocniIgru();
							}
						}
					}

					while (ova.drugi == null) {
						Thread.sleep(5000);
						continue;
					}

					while (ova.igra.isAlive() && this.uSobi == true)
						continue;
					Main.sobe.remove(ova);
					break;
				case 4:
					ispisPorukeOdServera("Unesite ime prijatelja: ");
					String prijatelj = unos();
					if (postojiUBazi(prijatelj)) {
						napraviZahtevZaPrijateljstvo(prijatelj);
						for (ClientHandler korisnik : Main.onlineKorisnici) {
							if (korisnik.username.equals(prijatelj))
								korisnik.zahtevi.add(new ZahtevZaPrijateljstvo(this.username, 0));
						}
						ispisPorukeOdServera("Zahtev je poslat");

					} else
						ispisPorukeOdServera("Prijatelj ne postoji");
					break;

				case 5:
					proveriZahteve();
					break;
				case 6:// logout

				default:

				}

			} while (izbor != 6);
			clientOutput.println("Kraj");
			unos();
			Main.onlineKorisnici.remove(this);
			socketZaKomunikaciju.close();

		} catch (IOException e) {

			Main.onlineKorisnici.remove(this);

			for (ClientHandler client : Main.onlineKorisnici) {

				if (client != this) {
					client.clientOutput.println(">>> Korisnik " + username + " je napustio/la u chat!!!");
				}
			}
		} catch (InterruptedException e) {
		}

	}

	private void napraviZahtevZaPrijateljstvo(String ime) {
		zahtevi.add(new ZahtevZaPrijateljstvo(ime, 0));
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			PreparedStatement st = con.prepareStatement("INSERT INTO Friends(USER_ID1, USER_ID2) VALUES(?,?)");
			st.setInt(1, this.idIgraca);
			st.setInt(2, getIDprijatelja(ime));
			st.execute();
			st.close();
			con.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	private void proveriZahteve() throws IOException {
		for (ZahtevZaPrijateljstvo zahtevZaPrijateljstvo : zahtevi) {
			ispisPorukeOdServera("Username: " + zahtevZaPrijateljstvo.usernamePrijatelja + " status: "
					+ zahtevZaPrijateljstvo.status);
			ispisPorukeOdServera("Da li prihvatate zahtev(y/n)");
			if (unos().toLowerCase().equals("y")) {
				zahtevZaPrijateljstvo.status = 1;
				for(ClientHandler c : Main.onlineKorisnici) {
					for(ZahtevZaPrijateljstvo z:c.zahtevi) {
						if(c.username.equals(z.usernamePrijatelja))
							z.status=1;
					}
				}
				try {
					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
					PreparedStatement st = con.prepareStatement(
							"update friends " + "set fstatus = 1 where user_id1 = ? and user_id2 = ?");
					System.out.println(getIDprijatelja(zahtevZaPrijateljstvo.usernamePrijatelja) + " " + this.idIgraca);
					st.setInt(1, getIDprijatelja(zahtevZaPrijateljstvo.usernamePrijatelja));
					st.setInt(2, this.idIgraca);
					st.execute();
					st.close();
					con.close();

				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private void registracija() throws IOException {
		clientOutput.println("Unesite korisnicko ime za registraciju");
		while (true) {
			String username = clientInput.readLine();
			if (postojiUBazi(username)) {
				clientOutput.println("Username vec postoji. Unesite ponovo.");
			} else {
				upisiUsera(username);
				clientOutput.println("Registracija uspjesna");
				break;
			}
		}

	}

	private void logovanje() throws IOException {
		clientOutput.println("Unesite vase korisnicko ime");
		while (true) {
			String username = clientInput.readLine();
			if (postojiUBazi(username)) {
				clientOutput.println("Uspjesno logovanje.");
				this.username = username;
				break;
			} else {
				clientOutput.println("Neuspjesno logovanje. Pokusajte ponovo.");
			}
		}
	}

	private void ispisiMeni() {
		clientOutput.println("Izaberi opciju:");
		clientOutput.println("1. Registracija");
		clientOutput.println("2. Logovanje");
	}

	public boolean postojiUBazi(String ime) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT NAME FROM USERS");
			while (rs.next()) {
				String name = rs.getString("name");
				if (name.equalsIgnoreCase(ime)) {
					return true;
				}
			}
			st.close();
			con.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void upisiUsera(String ime) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			PreparedStatement st = con.prepareStatement("INSERT INTO USERS(NAME) VALUES(?)");
			st.setString(1, ime);
			st.execute();
			ResultSet rs = st.executeQuery("SELECT USER_ID FROM USERS WHERE NAME = '" + ime + "'");
			while (rs.next()) {
				int id = rs.getInt(1);
				upisiStatistiku(id);
			}

			st.close();
			con.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void upisiStatistiku(int id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			PreparedStatement st = con.prepareStatement("INSERT INTO STATISTICS(USER_ID) VALUES(?)");
			st.setInt(1, id);
			st.execute();
			st.close();
			con.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public void upisi(String ime) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			PreparedStatement pstat = con.prepareStatement("INSERT INTO qqq(j) VALUES(?)");

			// pstat.setInt(1, 1);

			pstat.setString(1, ime);
			// Statement st = con.createStatement();
			// String ins = "INSERT INTO QQQ VALUES('"+ime+"')";
			pstat.execute();
			pstat.close();
			con.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
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

	private void slanjeprivatnePoruke(String poruka, String username) {
		for (ClientHandler klijent : Main.onlineKorisnici) {
			if (klijent.username.equals (username)) {
				if(klijent.uPrivateChatu==true)
				klijent.clientOutput.println(poruka);
			}
		}
	}

	private void ispisiOnline() throws IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			
			
			
			ResultSet rs =null;
			PreparedStatement st = con.prepareStatement("select distinct u.user_id, u.name, f.fstatus from users u join friends f on (u.user_id = f.user_id1 or u.user_id = f.user_id2)"+  
					" where user_id in(select user_id2 from friends where user_id1 in(Select user_id from users where name =?))" +
					"or user_id in (select user_id1 from friends where user_id2 in(Select user_id from users where name =?))");
			st.setString(1, username);
			st.setString(2, username);
			rs=st.executeQuery();
			while (rs.next()) {
				String name = rs.getString(2);
				int id = rs.getInt(1);
				int fs = rs.getInt(3);
				if (fs==1) {
					for (ClientHandler klijent : Main.onlineKorisnici) {
						if(name.equals(klijent.username)) {
							ispisPorukeOdServera(klijent.username);
							ispisPorukeOdServera("Da li zelite da komunicirate sa ovim prijateljem(y/n)");
							if(unos().toLowerCase().equals("y")) {
								ispisPorukeOdServera("Za izlaz unesite \"izlaz\"");
								this.uPrivateChatu=true;
							String poruka="";
							while(true) {
								poruka = unos();
								if(poruka.startsWith("izlaz".toLowerCase()))
									break;
								slanjeprivatnePoruke(poruka, name);
							}
							this.uPrivateChatu=false;
							}
						}
					}
				}
			}
			st.close();
			con.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	private void ispisMenija() {

		clientOutput.println(
				"1. Globalni chat\n2. Privatni chat\n3. Igraj\n4. Dodaj prijatelja\n5. Zahtevi za prijateljstvo\n6. Logout\nVas izbor:");

	}

	public String unos() throws IOException {
		return clientInput.readLine();
	}

	public void getID() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM USERS");
			while (rs.next()) {
				String name = rs.getString("name");
				if (name.equalsIgnoreCase(username)) {
					String id = rs.getString(1);
					this.idIgraca = Integer.parseInt(id);
				}
			}
			st.close();
			con.close();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

	public int getIDprijatelja(String ime) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT * FROM USERS");
			while (rs.next()) {
				String name = rs.getString("name");
				if (name.equalsIgnoreCase(ime)) {
					String id = rs.getString(1);
					return Integer.parseInt(id);
				}
			}
			st.close();
			con.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return -1;

	}

}
