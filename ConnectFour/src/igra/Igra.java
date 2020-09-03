package igra;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import clienthandler.ClientHandler;

public class Igra extends Thread {

	// public char[][] matrica = new char[8][8];
	public int[][] matrica = new int[8][8];
	public int pobednikID, gubitnikID;
	public ClientHandler prvi, drugi;
	public int pobednik=0; 
	public boolean revans=false;
	public Igra() {
	}

	public Igra(ClientHandler prvi, ClientHandler drugi, Soba soba) {
		this.prvi = prvi;
		this.drugi = drugi;
		
	}

	public void napuniMatricu() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				matrica[i][j] = 0;
			}
		}
	}

	public boolean proveraPobede(int rb_igraca) {
		// po horizontali
	//	int brojac = 0;
		for (int i = 0; i < matrica.length; i++) {
			for (int j = 0; j < matrica[i].length - 3; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i][j + 1] == rb_igraca && matrica[i][j + 2] == rb_igraca
						&& matrica[i][j + 3] == rb_igraca) {
					// System.out.println("Po horizontali: " + i);
					return true;
				}

			}
		}
		// po vertikali
		for (int i = 0; i < matrica.length - 3; i++) {
			for (int j = 0; j < matrica[i].length; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i + 1][j] == rb_igraca && matrica[i + 2][j] == rb_igraca
						&& matrica[i + 3][j] == rb_igraca) {
					// System.out.println("Po vertikali: " + j);
					return true;
				}

			}
		}

		// u smeru glavne dijagonale
		for (int i = 0; i < matrica.length - 3; i++) {
			for (int j = 0; j < matrica[i].length - 3; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i + 1][j + 1] == rb_igraca
						&& matrica[i + 2][j + 2] == rb_igraca && matrica[i + 3][j + 3] == rb_igraca) {
					// System.out.println("Po glavnoj dijag: ");
					return true;
				}
			}
		}
		// u smeru suprotne dijagonale

		for (int i = 3; i < matrica.length; i++) {
			for (int j = 0; j < matrica[i].length - 3; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i - 1][j + 1] == rb_igraca
						&& matrica[i - 2][j + 2] == rb_igraca && matrica[i - 3][j + 3] == rb_igraca) {
					// System.out.println("Po sporednoj dijag: ");
					return true;
				}
			}
		}
		return false;
	}

	public int postavi(int prva, int igrac) {
	
		// 0 sve ok, 1 ponovni unos, 2 nema mesta-kraj
		int brojac = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (matrica[i][j] == 0)
					brojac++;
			}
		}
		if (brojac == 0)
			return 2;
		if (matrica[0][prva] != 0)
			return 1;
		for (int i = 7; i >= 0; i--) {
			if (matrica[i][prva] == 0) {
				matrica[i][prva] = igrac;
				break;
			}
		}
		return 0;
	}


	@Override
	public void run() {
		do {
		napuniMatricu();
		prvi.ispisPorukeOdServera("Igra pocinje...");
		drugi.ispisPorukeOdServera("Igra pocinje...");
		do {
			int provera = 0;
			synchronized (matrica) {
				prvi.ispisPorukeOdServera("Unesi kolonu");
				int kolona;
				
				do {
					while (true) {
						try {
							kolona = Integer.parseInt(prvi.unos());
							break;
						} catch (NumberFormatException | IOException e) {
							prvi.ispisPorukeOdServera("Pogresan unos");
						}
					}

					provera = postavi(kolona, 1);
					if (provera == 1) {
						prvi.ispisPorukeOdServera("Kolona je popunjena, ne mozete tu vise da ubacujete");
					} else if (provera == 2) {
						prvi.ispisPorukeOdServera("Kraj igre-nereseno");
						drugi.ispisPorukeOdServera("Kraj igre-nereseno");
						return;
					}

				} while (provera != 0 && provera != 2);
				
			}
				prvi.ispisMatrice(this);
				drugi.ispisMatrice(this);
			
			if (proveraPobede(1)) {
				pobednik=1;
				prvi.ispisPorukeOdServera("Pobedili ste!");
				drugi.ispisPorukeOdServera("Prvi je pobedio");
				break;
				//return;
			}

			synchronized (matrica) {
				drugi.ispisPorukeOdServera("Unesi kolonu");
				int kolona;
				provera = 0;
				do {
					while (true) {
						try {
							kolona = Integer.parseInt(drugi.unos());
							break;
						} catch (NumberFormatException | IOException e) {
							drugi.ispisPorukeOdServera("Pogresan unos");
						}
					}

					provera = postavi(kolona, 2);
					if (provera == 1) {
						drugi.ispisPorukeOdServera("Kolona je popunjena, ne mozete tu vise da ubacujete");
					} else if (provera == 2) {
						prvi.ispisPorukeOdServera("Kraj igre-nereseno");
						drugi.ispisPorukeOdServera("Kraj igre-nereseno");
						return;
					}

				} while (provera != 0 && provera != 2);
				
			}
				if(provera==2)break;
				prvi.ispisMatrice(this);
				drugi.ispisMatrice(this);
			
			if (proveraPobede(2)) {
				pobednik=2;
				prvi.ispisPorukeOdServera("Drugi je pobedio");
				drugi.ispisPorukeOdServera("Pobedili ste!");
				break;
				//return;
			}
			
		} while (true);
		this.upisiIgru();
		this.updateStatistiku();
		revans=false;
		synchronized(this) {
			prvi.ispisPorukeOdServera("Da li zelite revans(y/n)");
			try {
				if(prvi.unos().equals("y".toLowerCase())) {
					revans=true;
					drugi.ispisPorukeOdServera("Prvi zeli revans");
				}else
					drugi.ispisPorukeOdServera("Prvi ne zeli revans");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		synchronized(this) {
			drugi.ispisPorukeOdServera("Da li zelite revans(y/n)");
			try {
				if(revans) {
				if(drugi.unos().equals("y".toLowerCase())) {
					revans=true;
					prvi.ispisPorukeOdServera("drugi igrac takodje zeli revans");
				}else {
					prvi.ispisPorukeOdServera("Drugi ipak ne zeli revans");
					revans=false;
				}
				}
				} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		}while(revans==true);
	}
	
	public void upisiIgru() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			PreparedStatement st = con.prepareStatement("INSERT INTO game(user_id1, user_id2, date_time, result) VALUES(?,?,?,?)");
			st.setInt(1, prvi.idIgraca);
			st.setInt(2, drugi.idIgraca);
			java.util.Date date =  new java.util.Date();
			Timestamp sqlTimeStamp =  new java.sql.Timestamp(date.getTime());
			st.setTimestamp(3, sqlTimeStamp);
			if(pobednik==1)
				st.setString(4, "first_won");
			else if (pobednik==2)
				st.setString(4, "second_won");
			else 
				st.setString(4, "draw");
			st.execute();
			st.close();
			con.close();
			
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateStatistiku() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root", "root");
			PreparedStatement st1 = null;
			PreparedStatement st2 = null;
			PreparedStatement st3 = null;
			PreparedStatement st4 = null;
			
			if(pobednik==1) {
				st1 = con.prepareStatement("update  statistics set wins=wins+1 where user_id = ?");
			st2 = con.prepareStatement("update  statistics set loses=loses+1 where user_id = ?");
			st3 = con.prepareStatement("update  statistics set raiting=raiting+5 where user_id = ?");
			st4 = con.prepareStatement("update  statistics set raiting=raiting-2 where user_id = ?");
			st1.setInt(1, prvi.idIgraca);
			st2.setInt(1, drugi.idIgraca);
			st3.setInt(1, prvi.idIgraca);
			st4.setInt(1, drugi.idIgraca);
			st1.execute();
			st1.close();
			st2.execute();
			st2.close();
			st3.execute();
			st3.close();
			st4.execute();
			st4.close();
			}
			else if (pobednik==2)
				{
				st1 = con.prepareStatement("update  statistics set wins=wins+1 where user_id = ?");
				st2 = con.prepareStatement("update  statistics set loses=loses+1 where user_id = ?");
				st3 = con.prepareStatement("update  statistics set raiting=raiting-2 where user_id = ?");
				st4 = con.prepareStatement("update  statistics set raiting=raiting+5 where user_id = ?");
				st3.setInt(1, prvi.idIgraca);
				st4.setInt(1, drugi.idIgraca);
				st1.setInt(1, drugi.idIgraca);
				st2.setInt(1, prvi.idIgraca);
				st1.execute();
				st1.close();
				st2.execute();
				st2.close();
				st3.execute();
				st3.close();
				st4.execute();
				st4.close();
				}
			else {
				st1 = con.prepareStatement("update  statistics set draws=draws+1 where user_id = ?");
				st2 = con.prepareStatement("update  statistics set draws=draws+1 where user_id = ?");
				st3 = con.prepareStatement("update  statistics set raiting=raiting+1 where user_id = ?");
				st4 = con.prepareStatement("update  statistics set raiting=raiting+1 where user_id = ?");
				st1.setInt(1, prvi.idIgraca);
				st2.setInt(1, drugi.idIgraca);
				st3.setInt(1, prvi.idIgraca);
				st4.setInt(1, drugi.idIgraca);
				st1.execute();
				st1.close();
				st2.execute();
				st2.close();
				st3.execute();
				st3.close();
				st4.execute();
				st4.close();
			}
				
			con.close();
			
			
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
	
}
