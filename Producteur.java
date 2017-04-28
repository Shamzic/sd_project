import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Producteur extends Remote
{
        
    // Renvoie la quantité    
    public int askRessourceAmount(int ressource)
        throws RemoteException ;
        
    public SerializableList<TYPE> getStockTypes()
        throws RemoteException ;
    
    public TYPE getStockType(int rNumber)
        throws RemoteException ;

    public void decreaseRessourceAmount( int ressource, int x)
        throws RemoteException ;
        
    public void fonctionThread ( int ms )
        throws RemoteException ;
    
    public int getStock(int quantity, TYPE T)
        throws RemoteException ;

    public SerializableList<Tuple<TYPE,Integer>> getStock()
        throws RemoteException;
    public void end()
        throws RemoteException;
}
