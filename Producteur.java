import java.util.ArrayList;
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Producteur extends Remote
{
    public void salut()
        throws RemoteException ;
        
    // Renvoie la quantité    
    public int askRessourceAmount(int ressource)
        throws RemoteException ;
        
    public SerializableList<TYPE> getRessourceTypes()
        throws RemoteException ;
    
    public TYPE getRessourceType(int rNumber)
        throws RemoteException ;

    public void decreaseRessourceAmount( int ressource, int x)
        throws RemoteException ;
    
}
