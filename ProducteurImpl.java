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
        for (i=0; i< RD; i++) // initialise toutes les ressources du producteur
            RList.add(i, new Ressource(RI));
        
        for (i=0; i< RD ; i++)
        {
            System.out.println("getRType " + getRessourceType(i));
        }
	}
	 // sert à rien 
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
    // renvoie la quantité de la ressource N°ressource 
    public int askRessourceAmount( int ressource)
        throws RemoteException 
    {
        return RList.get(ressource).getRessource();
    }
    
    // Renvoie le type de la ressource n°rNumber du tableau du producteur
    public TYPE getRessourceType( int rNumber)
    {
        return RList.get(rNumber).getRessourceType();
    }

    // Renvoie une liste contenant tous les types du producteur
    public SerializableList<TYPE> getRessourceTypes()
    {
        int i;
        SerializableList<TYPE> L = new SerializableList<TYPE>();;
        for(i=0 ; i < RList.size() ; i++)
            L.add( RList.get(i).getRessourceType() );
        return L;
    }

    public void decreaseRessourceAmount(int ressource, int x)
        throws RemoteException 
    {
        RList.get(ressource).decreaseRessource(x);
    }
    
}

