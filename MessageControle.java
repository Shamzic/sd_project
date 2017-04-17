
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 
import java.util.ArrayList;


public interface MessageControle extends Remote
{
    public int getIdProducteur()
        throws RemoteException ;
        
    public InitialInfoImpl getPlayerInitialInfo()
        throws RemoteException;
        
    public InitialInfoImpl getProducteurInitialInfo()
        throws RemoteException;

    public void addMachine( String MachineName, int port)
        throws RemoteException;

    public void addProducteur( String MachineName, int port)
        throws RemoteException;
    
    public SerializableList<SerializableList<TYPE>> getStocksTypesAllProducteurs()
        throws RemoteException;

    public void sendInformation(int idPlayer, SerializableList<Ressource> Ressources)
        throws RemoteException;
        
    public void GameEnded()
        throws RemoteException;
        
    public void deletePlayer( int id)
        throws RemoteException;
}
