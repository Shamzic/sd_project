import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;


class JoueurImpl extends UnicastRemoteObject implements Joueur
{
    public int id, RD, RI;
    
	JoueurImpl(int id, int RI, int RD)
    throws RemoteException
	{
		this.id = id;
        this.RD = RD;
        this.RI = RI;
	}
	
	
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
}

