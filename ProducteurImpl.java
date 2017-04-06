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
	
    public void salut() // sert à rien 
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
    // demande le montant de la ressource <ressource> 
    public int askRessourceAmount( int ressource)
        throws RemoteException 
    {
        return RList.get(ressource).getRessource();
    }
    
    
    public TYPE getRessourceType( int rNumber)
    {
        return RList.get(rNumber).getRessourceType();
    }

    public SerializableList<TYPE> getRessourceTypes()
    {
        int i;
        SerializableList<TYPE> L = new SerializableList<TYPE>();;
        for(i=0 ; i < RList.size() ; i++)
            L.add( RList.get(i).getRessourceType() );
        return L;
    }
    
}

