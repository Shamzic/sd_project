import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Joueur extends Remote
{
    public void salut()
        throws RemoteException ;
        
    public void increaseRessourceAmout(TYPE t, int x)
        throws RemoteException ;
    
    
    public void receiveToken()
        throws RemoteException ;
//~ 
    //~ public void start()
        //~ throws RemoteException ;

    public  void start()
    throws RemoteException;

    public void askProdForRessource(int productorNumber, TYPE t, int quantity)
    throws RemoteException;

    public void displayRessourceList()
    throws RemoteException;
    
}
