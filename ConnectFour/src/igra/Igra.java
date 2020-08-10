package igra;

import java.io.InputStreamReader;
import java.util.Scanner;

public class Igra {

	//public char[][] matrica = new char[8][8];
	public int[][] matrica = new int[8][8];
	public void napuniMatricuZaTest() {
		int a = 1;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				//matrica[i][j] = '*';
				matrica[i][j]=0;
			
			}

		}
	}
	 
	public void ispisiMatricu() {
		for (int i = 0; i < 8; i++) {
			
			for (int j = 0; j < 8; j++) {
				System.out.print(matrica[i][j] + "   ");
			}
			System.out.println();
		}

	}

	public void ispis() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				System.out.print(matrica[i][j]+"\t");
			}
			System.out.println();
		}
	}

	public boolean proveraPobede(int rb_igraca) {
		// po horizontali
		int brojac = 0;
		for (int i = 0; i < matrica.length; i++) {
			for (int j = 0; j < matrica[i].length-3; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i][j+1] == rb_igraca &&
						matrica[i][j+2] == rb_igraca && matrica[i][j+3] == rb_igraca ) {
					//System.out.println("Po horizontali: " + i);
					return true; }
				
				}
			}
		// po vertikali
		for (int i = 0; i < matrica.length-3; i++) {
			for (int j = 0; j < matrica[i].length; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i+1][j]==rb_igraca &&
						matrica[i+2][j] == rb_igraca && matrica[i+3][j]==rb_igraca) {
					//System.out.println("Po vertikali: " + j);
					return true;
				}
				
				}
			}
			
		//u smeru glavne dijagonale
		for (int i = 0; i < matrica.length-3; i++) {
			for (int j = 0; j < matrica[i].length-3; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i+1][j+1]==rb_igraca &&
						matrica[i+2][j+2] == rb_igraca && matrica[i+3][j+3]==rb_igraca){
					//System.out.println("Po glavnoj dijag: ");
					return true;
				}
				}
			}
		//u smeru suprotne dijagonale
		
		for (int i = 3; i < matrica.length; i++) {
			for (int j = 0; j < matrica[i].length-3; j++) {
				if (matrica[i][j] == rb_igraca && matrica[i-1][j+1]==rb_igraca &&
						matrica[i-2][j+2] == rb_igraca && matrica[i-3][j+3]==rb_igraca){
				//	System.out.println("Po sporednoj dijag: ");
					return true;
				}
				}
			}
		return false;
	}

	public int postavi(int prva, int igrac) {
		//matrica[prva][druga]=(char)(igrac+'0');
		//0 sve ok, 1 ponovni unos, 2 nema mesta-kraj
		int brojac = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
			if(matrica[i][j]==0)brojac++;
			}
			}
		if(brojac==0)return 2;
		if(matrica[0][prva]!=0)return 1;
		for(int i=7;i>=0;i--) {
			if(matrica[i][prva]==0) {
				matrica[i][prva]=igrac;
				break;
			}
		}
		//matrica[prva][druga]=igrac;
		return 0;
	}
	
	public static void main(String[] args) {
//		Igra i = new Igra();
//		i.matrica[0][0]=1;i.matrica[0][1]=0;i.matrica[0][2]=10;i.matrica[0][3]=0;
//		i.matrica[1][0]=0;i.matrica[1][1]=8;i.matrica[1][2]=0;i.matrica[1][3]=1;
//		i.matrica[2][0]=0;i.matrica[2][1]=0;i.matrica[2][2]=1;i.matrica[2][3]=1;
//		i.matrica[3][0]=0;i.matrica[3][1]=1;i.matrica[3][2]=1;i.matrica[3][3]=1;
//		if(i.proveraPobede(1))
//			System.out.println("prvi pobedio");
//		if(i.proveraPobede(0))
//			System.out.println("drugi pobedio");
//		String c="2";
//		
//		boolean prov=false;
//		Scanner s =  new Scanner(new InputStreamReader(System.in));
//		c=s.next();
//		do {
//			c=s.next();
//			char q[] = c.toCharArray();
//			if(q.length==1 && Character.isDigit(q[0]))
//			prov=true;
//			if(prov==false)
//			System.out.println("Unesi ga  opet debilu");
//			
//			
//		}while(prov==false);
	}
}
