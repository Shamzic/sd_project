import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Connexion extends Remote
{   
     public void initialSetPlayer( SerializableList<Tuple> L)
        throws RemoteException ;
    
    public void addConnexionPlayer(String MachineName, int port)
        throws RemoteException ;
    
    public void addConnexionProducteur(String MachineName, int port)
        throws RemoteException ;
        
    public void setProducteur ( SerializableList<Tuple> PCoordList)
        throws RemoteException ;
        
    public int getStockAmount( int producteurNb, int ressourceNb)
        throws RemoteException;

    // Soustrait un nombre de ressource à un producteur
    // et l'ajoute à un joueur
    public void takeRessourceAmount( int producteurNb, int ressourceNb, int quantite)
    throws RemoteException;
}
