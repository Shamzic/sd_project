import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

public interface Joueur extends Remote
{
    public void increaseRessourceAmout(TYPE t, int x)
        throws RemoteException ;
    
    public void receiveToken()
        throws RemoteException ;

    public  void start()
    	throws RemoteException;

    public int getStock(int quantity,TYPE t)
    throws RemoteException;

    public void askProdForRessource(int productorNumber, TYPE t, int quantity)
    	throws RemoteException;

    public void displayRessourceList()
   		throws RemoteException;
        
    public void end()
        throws RemoteException;
        
    public int getId()
        throws RemoteException;

    public void deletePlayer(int id)
        throws RemoteException;
}
