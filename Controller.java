import java.net.* ;
import java.rmi.* ;

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
 * nb de ressourcse pour gagner
 * mode jeu
 * ( en fonction du mode ) temps de jeu
 * 
*/
public class Controller
{
	int nbJoueurs, nbProducteurs;
    int nbRessourcesInitiales;
    int nbRessourcesDifferentes;
    
	Controller (String args[])
	{
		nbJoueurs = Integer.parseInt( args[0] ); // nombre de joueur qu'on veut pour lancer la partie
        nbProducteurs = Integer.parseInt( args[1] ); // nombre de producteurs qu'on veut
        nbRessourcesInitiales = 5; // amount de chaque ressource à l'état initial
        nbRessourcesDifferentes = 3; // nombre de ressources différentes de chaque producteur

		try
		{
            
			// Commence par faire l'objet grâce auquel le Controlleur communique avec les agents
            MessageControleImpl MC = new MessageControleImpl(5,3, nbProducteurs,nbJoueurs,"localhost", 5000);
            Naming.rebind( "rmi://localhost:"+5000 + "/MessageControleGlobal", MC); 
            
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
	}

}
