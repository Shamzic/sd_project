import java.util.ArrayList;
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Connexion extends Remote
{
    public void initialSet( SerializableList L)
        throws RemoteException ;
    
    public void addConnexion(String MachineName, int port)
        throws RemoteException ;
}
