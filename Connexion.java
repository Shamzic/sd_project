import java.util.ArrayList;
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
        
    public int getRessourceAmount( int producteurNb, int ressourceNb)
        throws RemoteException;

    public int takeRessourceAmount( int producteurNb, int ressourceNb)
    throws RemoteException;
}
