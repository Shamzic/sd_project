import java.util.ArrayList;
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Producteur extends Remote
{
    public void salut()
        throws RemoteException ;
        
    public int askRessourceAmount( int ressource)
        throws RemoteException ;
        
    public SerializableList<TYPE> getRessourceTypes()
        throws RemoteException ;
    
    public TYPE getRessourceType( int rNumber)
        throws RemoteException ;
        
    public void fonctionThread ( int ms, int quantity)
        throws RemoteException ;
    
}
