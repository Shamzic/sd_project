import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

// objet qui va contenir toutes les connexions (objets avec les autres agents)
// Une méthode de cette classe est appelée à chaque fois que le controlleur ajoute une machine
class ConnexionImpl extends UnicastRemoteObject implements Connexion
{
    public SerializableList L ;
    public ArrayList<Joueur> JList = new ArrayList<Joueur>();
    
    public ArrayList<Producteur> PList = new ArrayList<Producteur>();

    
    public ConnexionImpl()
    throws RemoteException
    {
    }
    
    public int getRessourceAmount( int producteurNb, int ressourceNb)
    throws RemoteException
    {
        return PList.get(producteurNb).askRessourceAmount(ressourceNb);
    }
    
    
    public void initialSetPlayer( SerializableList L)
    throws RemoteException
    {
        int i;
        this.L = L;
        try
        {
            for( i = 0 ; i< L.size() ; i++)
            {
                System.out.println("Machine :" + L.get(i).MN + " :" + L.get(i).port);
                Joueur P = (Joueur) Naming.lookup("rmi://" +  L.get(i).MN + ":" + L.get(i).port + "/Joueur") ;
                JList.add( P );
                P.salut();
            }
            
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
    
    public void setProducteur ( SerializableList PCoordList)
    throws RemoteException
    {
        int i;
        int size = PCoordList.size();
        try
        {
            for(i=0; i < size ; i++)
            {
                System.out.println("Ajoute le producteur " + i + " au port " + PCoordList.get(i).port + " nom de machine : " + PCoordList.get(i).MN);
                Producteur P = (Producteur) Naming.lookup("rmi://" +  PCoordList.get(i).MN + ":"+PCoordList.get(i).port+"/Producteur");
                PList.add(P);
            }
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
    
    public void addConnexionPlayer(String MachineName, int port)
    throws RemoteException
    {
        L.add(MachineName,port);
        System.out.println("J'ai ajouté la machine " + MachineName + ":"+ port);
        try
        {
            Joueur P = (Joueur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Joueur") ;
            JList.add( P );
            P.salut();
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }
	
}
