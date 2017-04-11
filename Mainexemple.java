
// Main qui lance le contrôleur
/*
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
*/

public class Mainexemple
{

	public static void main ( String [] args)
	{
	        Parser p = new Parser();
	        p.lecture_fichier("configuration");
	        String nbP = p.getNbProducteurs();
	        String nbJ = p.getNbJoueurs();
	        String []S = {nbJ,nbP};
	        System.out.println("*** Configuration du jeu ***");
	        System.out.println("Nombre de producteurs : "+nbP);
	        System.out.println("Nombre de joueurs : "+nbJ);
	        Controller C = new Controller(S);
	}
}


