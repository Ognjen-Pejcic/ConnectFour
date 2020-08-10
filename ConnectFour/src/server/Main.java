package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import clienthandler.ClientHandler;
import igra.Igra;

public class Main{

	public static LinkedList<ClientHandler> onlineKorisnici = new LinkedList<>();
	
	public static LinkedList<LinkedList<ClientHandler>> sobe = new LinkedList<>();
	public static void main(String[] args) {
		int portNumber = 12000;
		Socket socketZaKomunikaciju = null;
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(portNumber);
			while(true) {
	
				System.out.println("Cekam na komunikaciju...");
				socketZaKomunikaciju = serverSocket.accept();
				System.out.println("Broj online: " +onlineKorisnici.size());
			//	Thread.sleep(1000);
				if(onlineKorisnici.size()<2) {
					//System.out.println("Igra pocinje");
					
				
					/*
					 * System.out.println("Cekam na komunikaciju..."); socketZaKomunikaciju =
					 * serverSocket.accept();
					 */
					System.out.println("Doslo je do konekcije!");
					
					ClientHandler klijent = new ClientHandler(socketZaKomunikaciju);
					onlineKorisnici.add(klijent);
				/*	if(onlineKorisnici.size()%2==0) {
						sobe.add(new LinkedList<>());
						for (LinkedList<ClientHandler> soba : sobe) {
							if(soba.size()==0) 
								soba.add(klijent);
						}
					}else 
						for (LinkedList<ClientHandler> soba : sobe) {
							if(soba.size()==1) 
								soba.add(klijent);
						}*/
				
				
					klijent.start();
				
				}else {
					continue;
				}
				
		
				
			}
		} catch (Exception e) {
			System.out.println("Greska prilikom pokretanja servera");
		}
			
	
		
		
		}
	

	
}
