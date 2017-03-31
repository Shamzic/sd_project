import java.util.ArrayList;


/* Arguments du Controlleur
 * port rmiregistry du controlleur
 * fichier d'enregistrement d'évènements
 * nom de machine
 * nb de joueurs 
 * [] liste de port rmiregistry pour joueurs
 * nb de producteurs
 * [] liste de port rmiregistry pour producteurs
 * ressources initiales
 * nb de ressources différentes
 * nb de ressources nécessaires pour gagner
 * mode jeu
 * ( en fonction du mode ) temps de jeu
 * 
*/
public class Controller
{
	
	
	Controller (String args[])
	{
		ArrayList<Joueur> Joueurs;
		ArrayList<Producteur> Producteurs;
		int nbJoueurs, nbProducteurs;
		int i ;
		Joueur tmp;
		Producteur tmp2;
		
		if (args.length != 2)
		{
			System.out.println("Controller : <nbJoueurs> <nbProducteurs>");
			System.exit(1);
		}
		nbJoueurs = Integer.parseInt( args[0] );
		nbProducteurs = Integer.parseInt( args[1] );
		
		//try
		//{
			// fait autant de joueurs et producteurs spécifiés dans les arguments
			Joueurs = new ArrayList<Joueur>( nbJoueurs );
			Producteurs = new ArrayList<Producteur>( nbProducteurs );
			for( i = 0 ; i < nbJoueurs ; i++)
			{
				tmp = new Joueur(i);
				Joueurs.add(i,tmp);
			}
			for( i = 0 ; i < nbProducteurs ; i++)
			{
				tmp2 = new Producteur(i);
				Producteurs.add(i,tmp2);
			}
			
			// Commence par faire le corba serveur
			
			
			
			// Maintenant il faut faire un corba avec tous les agents
			// puis il faut transmettre tous les corba a tous les agents
		//}
		
	}
	
	
	
	
}
