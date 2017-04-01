import java.util.ArrayList;
import java.lang.Runtime;

import java.net.* ;
import java.rmi.* ;
import java.io.* ;

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
		int i ;
		nbJoueurs = Integer.parseInt( args[0] );
        nbProducteurs = Integer.parseInt( args[1] );
        nbRessourcesInitiales = 5;
        nbRessourcesDifferentes = 3;

        // fait autant de messages de contrôles que spécifiés dans les arguements
        // doit d'abord attendre que les joueurs envoient un message de confirmation 
        // d'initialisation au controlleur pour qu'il puisse se connecter aux agents
	//	ArrayList<MessageControleImpl> Joueurs = new ArrayList<MessageControleImpl>( nbJoueurs );
		//ArrayList<MessageControleImpl>  Producteurs = new ArrayList<MessageControleImpl>( nbProducteurs );
        
        
		
		try
		{
            
			// Commence par faire l'objet grâce auquel le Controlleur communique avec les agents
            MessageControleImpl MC = new MessageControleImpl(5,3, nbProducteurs);
            Naming.rebind( "rmi://localhost:"+5000 + "/MessageControleGlobal", MC);
            
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
	}
	
	
	
	
}
