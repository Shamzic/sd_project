import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

class ProducteurImpl extends UnicastRemoteObject implements Producteur
{
    public ArrayList<Ressource> RList;
    int id;
	ProducteurImpl (int id, int RI, int RD)
    throws RemoteException
	{
        int i;
        this.id = id;
        RList = new ArrayList<Ressource> (RD);
        for (i=0; i< RD; i++)
            RList.add(i, new Ressource(RI));
        
        
	}
	
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
    public int askRessourceAmount( int ressource)
        throws RemoteException 
    {
        return RList.get(ressource).getRessource();
    }
}

