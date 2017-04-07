import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

class JoueurImpl extends UnicastRemoteObject implements Joueur
{
    public int id, RD, RI;
    public ArrayList<Ressource> RList;
    static ConnexionImpl C;
    
	JoueurImpl(int id, int RI, int RD, String CoordinateurCoord)
    throws RemoteException
	{
		this.id = id;
        this.RD = RD; // Ressources différentes ??
        this.RI = RI; // Ressources initiales du joueur, inutile ???
        RList = new ArrayList<Ressource> (RD);

        // Boucle pour init la liste des ressources du joueur
        // Ayant des quantité nulles pour chaque type
        for (TYPE t : TYPE.values())
        {
        	RList.add(new Ressource(0,t));
        }
        // initialise le serveur connexion pour que le controlleur puisse lui envoyer les nouveaux connectés
        C = new ConnexionImpl();
        try
        {
            Naming.rebind( "rmi://localhost:"+ CoordinateurCoord + "/Connexion", C);
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        start();
    }
	
	// Incrémente de x la quantité de la ressource de type t
	public void increaseRessourceAmout(TYPE t, int x)
    throws RemoteException
    {
    	for(int i=0 ; i < RList.size() ; i++)
           if(RList.get(i).getStockType()==t)
           		RList.get(i).increaseRessource(x);
    }

	
    public void start()
    {
        while(true)
        {
            try
            {
                wait();
                System.out.println("À mon tour.");
                Thread.sleep(1000);
                C.JList.get(id +1).receiveToken();
            }
            catch (InterruptedException re) { System.out.println(re) ; }
        }
    }
    
    public void receiveToken()
    {
        notify();
    }
    
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
}

