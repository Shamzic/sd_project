import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;


class JoueurImpl extends UnicastRemoteObject implements Joueur
{
    public int id, RD, RI;
    public ArrayList<Ressource> RList;
    
	JoueurImpl(int id, int RI, int RD)
    throws RemoteException
	{
		this.id = id;
        this.RD = RD; // Ressources différentes ??
        this.RI = RI; // Ressources initiales du joueur, inutile ???
        RList = new ArrayList<Ressource> (RD);

        // Boucle pour init la liste des ressources du joueur
        // Ayant des quantité nulles pour chaque type

	}
	
	// Incrémente de x la quantité de la ressource de type t
	public void increaseRessourceAmout(TYPE t, int x)
    throws RemoteException
    {
    	;
    }

	
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
}

