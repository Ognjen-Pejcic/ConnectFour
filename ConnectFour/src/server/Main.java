package server;

import java.io.BufferedReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import clienthandler.ClientHandler;
import igra.Soba;

public class Main {

	public static LinkedList<ClientHandler> onlineKorisnici = new LinkedList<>();
	public static LinkedList<Soba> sobe = new LinkedList<>();
	Connection con;

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
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
//			Statement stmt = con.createStatement();
//			ResultSet rs = stmt.executeQuery("SELECT * FROM USERS");
//			while(rs.next()) {
//				String name = rs.getString("name");
//				System.out.println(name);
//			}
			System.out.println("Unesi igraca");
			String nesto = "naky";
			Statement stmt = con.createStatement();
			String dpop = "INSERT INTO USERS VALUES('" + nesto + "')";
			stmt.execute(dpop);
			stmt.close(); //OBEVEZNO
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
