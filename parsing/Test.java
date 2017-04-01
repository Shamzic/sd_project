public class Test{

	public static void main(String[] args) {

		Parser p = new Parser();
		// Parsing du fichier configuration
		p.lecture_fichier("configuration");
		String nbjoueurs = p.getNbJoueurs();
		String nbproducteurs = p.getNbProducteurs();
		System.out.println("NB JOUEURS : "+nbjoueurs);
		System.out.println("NB PRODUCTEURS : "+nbproducteurs);
		// Ici on a les valeurs entières issues du fichier configuration
		int int_nbjoueurs = Integer.parseInt(nbjoueurs); 
		int int_nbproducteurs = +Integer.parseInt(nbproducteurs);
	}
}
