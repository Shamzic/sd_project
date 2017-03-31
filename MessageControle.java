
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface MessageControle extends Remote
{
    public int getIdProducteur()
        throws RemoteException ;
    public TripleImpl getProdInitialInfo()
        throws RemoteException;
    
}
