
import java.util.ArrayList;
import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Joueur extends Remote
{
    public void salut()
        throws RemoteException ;
    
}
