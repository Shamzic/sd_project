
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface MessageControle extends Remote
{
    public int getIdProducteur()
        throws RemoteException ;
    public TripleImpl getPlayerInitialInfo()
        throws RemoteException;
        
    public void addMachine( String MachineName, int port)
        throws RemoteException;

    
}