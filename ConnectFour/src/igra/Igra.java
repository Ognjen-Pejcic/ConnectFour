package igra;

import java.io.IOException;

import clienthandler.ClientHandler;

public class Igra extends Thread {

	// public char[][] matrica = new char[8][8];
	public int[][] matrica = new int[8][8];
	ClientHandler prvi, drugi;
	Soba soba;
	public Igra() {
	}

	public Igra(ClientHandler prvi, ClientHandler drugi, Soba soba) {
		this.prvi = prvi;
		this.drugi = drugi;
		this.soba=soba;
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
					}

				} while (provera != 0 && provera != 2);
				
			}
				prvi.ispisMatrice(this);
				drugi.ispisMatrice(this);
			
			if (proveraPobede(1)) {
				prvi.ispisPorukeOdServera("Pobedili ste!");
				drugi.ispisPorukeOdServera("Prvi je pobedio");
				break;
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
					}

				} while (provera != 0 && provera != 2);
				
			}
				if(provera==2)break;
				prvi.ispisMatrice(this);
				drugi.ispisMatrice(this);
			
			if (proveraPobede(2)) {
				prvi.ispisPorukeOdServera("Drugi je pobedio");
				drugi.ispisPorukeOdServera("Pobedili ste!");
				break;
			}
			
		} while (true);
		prvi.uSobi=false;
		drugi.uSobi=false;
	}
}
