import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

// objet qui va contenir toutes les connexions (objets avec les autres agents)
// Une méthode de cette classe est appelée à chaque fois que le controlleur ajoute une machine
class ConnexionImpl extends UnicastRemoteObject implements Connexion
{
    public SerializableList L ;
    
    public ConnexionImpl()
    throws RemoteException
    {
    }
    
    public void initialSet( SerializableList L)
    throws RemoteException
    {
        this.L = L;
        int i;
        for( i = 0 ; i< L.size() ; i++)
            System.out.println("Machine :" + L.get(i).MN + " :" + L.get(i).port);
    }
    
    public void addConnexion(String MachineName, int port)
    throws RemoteException
    {
        L.add(MachineName,port);
        System.out.println("J'ai ajouté la machine " + MachineName + ":"+ port);
    }
	
}
