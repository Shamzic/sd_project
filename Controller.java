import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
public class Controller
{
    public Controller(String args[])
    {
		try
		{
            // Fait une liste de ressource qu'il faut pour gagner
            SerializableList<Ressource> L = new SerializableList<Ressource>();

            L.add(new Ressource(100,0)); // Argent
            L.add(new Ressource(100,1)); // Or
            L.add(new Ressource(100,2)); // bois

	    // Commence par faire l'objet grâce auquel le Controlleur communique avec les agents
            MessageControleImpl MC = new MessageControleImpl(5,3, Integer.parseInt(args[1]),Integer.parseInt(args[0]),"localhost",5000,0,L, 0);
            Naming.rebind( "rmi://localhost:"+5000 +"/MessageControleGlobal", MC); 
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
	}

}
