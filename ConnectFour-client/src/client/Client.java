package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable{

	 static Socket soketZaKomunikaciju = null;
	    static BufferedReader serverInput = null;
	    static PrintStream serverOutput = null;
	    static BufferedReader unosSaTastature = null;
	    
	 public static void main(String[] args) {
		
		 try {
	           
	            // Kada pokrenemo klijenta gadjamo localhost i port 12000 def. na serverskoj strani
			 	String ipAddress = args[0];
			 	String port = args[1];
			 
	            soketZaKomunikaciju = new Socket(ipAddress, Integer.parseInt(port));
	           
	            // inicijalizujemo tokove i unos sa tastature
	           
	            serverInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
	            serverOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());
	           
	            unosSaTastature = new BufferedReader(new InputStreamReader(System.in));
	           
	            // Ovde pokrecemo metodu RUN koja je def. nize
	           
	            new Thread(new Client()).start();
	           
	           
	            String input;
	           
	            // Dokle god stizu poruke, iste se ispisuju na strani klijenta
	            // Ako dodje poruka koja pocinje sa >>> Dovidjenja, a to je u slucaju da smo mi uneli ***quit, zatvara se
	            // soket za komunikaciju
	           
	            while(true) {
	                input = serverInput.readLine();
	               
	                System.out.println(input);
	               
	                if(input.startsWith("Kraj")) {
	                    break;
	                }
	            }
	           
	            // Zatvaranje soketa u slucaju kada napustamo chat
	           
	            soketZaKomunikaciju.close();
	           
	        // Obradjena su dva izuzetka:
	        // Prvi u slucacu da je nepoznat host tj. server na koji se kacimo
	        // Drugi u slucaju da server iznenada prestane sa radom npr.           
	           
	        } catch (UnknownHostException e) {
	            System.out.println("UNKNOWN HOST!");
	        } catch (IOException e) {
	            System.out.println("SERVER IS DOWN!!!");
	        }
	       
	    }
	   
	 
	    // U okviru RUN metode saljemo poruke koje klijent otkuca ka serveru
	 
	    @Override
	    public void run() {
	 
	       
	            try {
	               
	                String message;
	               
	                while(true) {
	                   
	                    message = unosSaTastature.readLine();
	                    serverOutput.println(message);
	                   
	                    // Ako otkucamo quit napustamo chat
	                   
	                    if(message.equals("quit")) {
	                    break;
	                    }
	                }
	               
	            } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	        }