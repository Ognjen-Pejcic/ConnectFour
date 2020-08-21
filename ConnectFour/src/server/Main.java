package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import clienthandler.ClientHandler;
import igra.Soba;

public class Main {

	public static LinkedList<ClientHandler> onlineKorisnici = new LinkedList<>();
	public static LinkedList<Soba> sobe = new LinkedList<>();

	public static void main(String[] args) {
		Main pro = new Main();
		pro.createConnection();
		int portNumber = 12000;
		Socket socketZaKomunikaciju = null;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
			while (true) {
				System.out.println("Cekam na komunikaciju...");
				socketZaKomunikaciju = serverSocket.accept();
				System.out.println("Broj online: " + onlineKorisnici.size());
				System.out.println("Doslo je do konekcije!");
				ClientHandler klijent = new ClientHandler(socketZaKomunikaciju);
				onlineKorisnici.add(klijent);
				klijent.start();
			}
		} catch (Exception e) {
			System.out.println("Greska prilikom pokretanja servera");
		}
	}
	
	void createConnection () {
		try {
			Class.forName("com⁩.⁨mysql⁩.⁨jdbc⁩.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/MYDB", "root", "root");
			System.out.println("Uspjesno");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
