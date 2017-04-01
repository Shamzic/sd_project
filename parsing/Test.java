import java.io.*;

public class Test{

	public static void main(String[] args) {

		Parser p = new Parser("configuration");
		int nbjoueurs = p.getNbJoueurs();
		int nbproducteurs = p.getNbProducteurs();
		System.out.println(" NB JOUEURS : "+nbjoueurs);
		System.out.println("NB PRODUCTEURS : "+nbproducteurs);
	}
}
